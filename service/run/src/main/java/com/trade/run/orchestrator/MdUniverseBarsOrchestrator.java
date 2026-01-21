package com.trade.run.orchestrator;

import com.trade.run.config.RunProperties;
import com.trade.run.model.UniverseTargetDto;
import com.trade.run.steps.FetchTargetsStep;
import com.trade.run.steps.IngestUniverseBarsStep;
import com.trade.run.steps.RunContext;
import com.trade.run.steps.SummarizeStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class MdUniverseBarsOrchestrator {

    private final RunProperties props;
    private final FetchTargetsStep fetchTargetsStep;
    private final IngestUniverseBarsStep ingestStep;
    private final SummarizeStep summarizeStep;

    public Mono<Void> runOnce() {
        var md = props.md();
        var ctx = new RunContext(md.market(), md.intervalCd(), md.limit(), md.maxInstruments());

        return fetchTargetsStep.execute(ctx)
                .flatMapMany(targets -> {
                    if (targets.isEmpty()) {
                        log.info("[RUN] no targets market={} intervalCd={}", ctx.market(), ctx.intervalCd());
                        return Flux.<UniverseTargetDto.UniverseTargetsResponse.Target>empty();
                    }
                    return Flux.fromIterable(targets);
                })
                .flatMap(t -> ingestStep.execute(ctx, t), md.concurrency())
                .collectList()
                .flatMap(results -> summarizeStep.execute(ctx, results));
    }
}
