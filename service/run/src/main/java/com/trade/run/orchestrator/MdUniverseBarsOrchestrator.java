package com.trade.run.orchestrator;

import com.trade.common.constant.JobStatus;
import com.trade.run.config.RunProperties;
import com.trade.run.model.UniverseTargetDto;
import com.trade.run.service.RunIdempotencyService;
import com.trade.run.service.RunWriter;
import com.trade.run.steps.FetchTargetsStep;
import com.trade.run.steps.IngestUniverseBarsStep;
import com.trade.run.steps.RunContext;
import com.trade.run.steps.SummarizeStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.trade.common.constant.staticConst.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MdUniverseBarsOrchestrator {

    private final RunProperties props;
    private final FetchTargetsStep fetchTargetsStep;
    private final IngestUniverseBarsStep ingestStep;
    private final SummarizeStep summarizeStep;

    private final RunWriter runWriter;
    private final RunIdempotencyService idempotencyService;

    private String buildJobKey(String jobType, String market, String intervalCd) {
        String day = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return jobType + "|" + market + "|" + intervalCd + "|" + day;
    }

    public Mono<Void> runOnce() {
        var md = props.md();

        int conc = (md.concurrency() == null || md.concurrency() < 1) ? 1 : md.concurrency();
        var baseCtx = new RunContext(md.market(), md.intervalCd(), md.limit(), md.maxInstruments(), null, null);
        String jobKey = buildJobKey("md-bars", baseCtx.market(), baseCtx.intervalCd());

        // 선점
        // SUCCESS만 막고, FAILED면 재실행 허용
        return Mono.fromCallable(() -> idempotencyService.acquireOrResume(jobKey))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(acquired -> {
                    if (!acquired) {
                        log.warn("[RUN] skip. already SUCCESS. jobKey={}", jobKey);
                        return Mono.empty();
                    }

                    // run_runs
                    return Mono.fromCallable(() -> runWriter.startRun(jobKey, baseCtx, md))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(runId -> {
                                idempotencyService.attachRunId(jobKey, runId);
                                RunContext ctx = baseCtx.withRun(jobKey, runId);

                                // FETCH_TARGETS
                                return runStepFetchTargets(ctx)
                                        .flatMapMany(targets -> Flux.fromIterable(targets))
                                        // INGEST
                                        .flatMap(t -> runStepIngestOne(ctx, t), conc) // 동시에 한 3개?
                                        .collectList()
                                        // SUMMARIZE + finish
                                        .flatMap(results -> runStepSummarizeAndFinish(ctx, results))
                                        .doOnSuccess(v -> {
                                            // 성공/부분성공/스킵이면 SUCCESS 확정
                                            // (실패면 runStepSummarizeAndFinish에서 FAILED로 처리)
                                            // 여기선 idempotency SUCCESS 처리
                                            idempotencyService.markSuccess(jobKey);
                                        })
                                        .onErrorResume(e ->
                                                Mono.fromRunnable(() -> {
                                                            runWriter.finishRun(ctx.runId(),
                                                                    JobStatus.FAILED.getStatus(),
                                                                    0, 0, 0,
                                                                    "run exception",
                                                                    JobStatus.RUN_EXCEPTION.getStatus(),
                                                                    e.toString());
                                                            idempotencyService.markFailed(jobKey);
                                                        })
                                                        .subscribeOn(Schedulers.boundedElastic())
                                                        .then(Mono.error(e))
                                        );
                            });
                });
    }

    private Mono<List<UniverseTargetDto.UniverseTargetsResponse.Target>> runStepFetchTargets(RunContext ctx) {
        return Mono.fromCallable(() -> runWriter.startStep(ctx.runId(), FETCH_TARGETS))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(stepId ->
                        fetchTargetsStep.execute(ctx)
                                .flatMap(targets ->
                                        Mono.fromRunnable(() -> runWriter.finishStepSuccess(stepId, targets.size(), targets.size(), 0))
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .thenReturn(targets)
                                )
                                .onErrorResume(e ->
                                        Mono.fromRunnable(() -> runWriter.finishStepFailed(stepId, FETCH_TARGETS_FAIL, e.toString()))
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .then(Mono.error(e))
                                )
                );
    }

    private Mono<IngestUniverseBarsStep.IngestResult> runStepIngestOne(RunContext ctx, UniverseTargetDto.UniverseTargetsResponse.Target t) {
        String stepName = INGEST_UNIVERSE_BARS + ":" + t.universeId();

        return Mono.fromCallable(() -> runWriter.startStep(ctx.runId(), stepName))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(stepId ->
                        ingestStep.execute(ctx, t)
                                .flatMap(r ->
                                        Mono.fromRunnable(() -> {
                                                    int success = r.success() ? 1 : 0;
                                                    int failed = r.success() ? 0 : 1;
                                                    runWriter.finishStepSuccess(stepId, 1, success, failed);
                                                })
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .thenReturn(r)
                                )
                                .onErrorResume(e ->
                                        Mono.fromRunnable(() -> runWriter.finishStepFailed(stepId, INGEST_FAIL, e.toString()))
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .thenReturn(new IngestUniverseBarsStep.IngestResult(t.universeId(), false, e.toString()))
                                )
                );
    }

    private Mono<Void> runStepSummarizeAndFinish(RunContext ctx, List<IngestUniverseBarsStep.IngestResult> results) {
        return Mono.fromCallable(() -> runWriter.startStep(ctx.runId(), SUMMARIZE))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(stepId -> {

                    int targets = results.size();
                    int success = (int) results.stream().filter(IngestUniverseBarsStep.IngestResult::success).count();
                    int failed = targets - success;

                    AtomicReference<String> summary = new AtomicReference<>("targets=" + targets + ", success=" + success + ", failed=" + failed);

                    // 로그용
                    Mono<Void> safeSummarize =
                            summarizeStep.execute(ctx, results)
                                    .onErrorResume(e -> {
                                        log.warn("[RUN] summarize failed but continue. runId={}, err={}", ctx.runId(), e.toString());
                                        return Mono.empty();
                                    });

                    return safeSummarize
                            .then(Mono.fromRunnable(() -> runWriter.finishStepSuccess(stepId, targets, success, failed))
                                    .subscribeOn(Schedulers.boundedElastic()))
                            .then(Mono.fromRunnable(() -> {
                                        String finalStatus;
                                        String errCode = null;
                                        String errMsg = null;

                                        if (targets == 0) {
                                            finalStatus = JobStatus.SKIPPED.getStatus();
                                            summary.set("no targets. " + summary);
                                        } else if (success == 0) {
                                            finalStatus = JobStatus.FAILED.getStatus();
                                            errCode = JobStatus.ALL_FAILED.getStatus();
                                            errMsg = "all universes failed";
                                        } else if (failed > 0) {
                                            finalStatus = JobStatus.PARTIAL_SUCCESS.getStatus();
                                        } else {
                                            finalStatus = JobStatus.SUCCESS.getStatus();
                                        }

                                        runWriter.finishRun(ctx.runId(),
                                                finalStatus,
                                                targets, success, failed,
                                                summary.get(),
                                                errCode,
                                                errMsg);
                                    })
                                    .subscribeOn(Schedulers.boundedElastic()))
                            .then();
                });
    }
}
