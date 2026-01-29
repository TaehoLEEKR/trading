package com.trade.run.steps;

import com.trade.run.model.IngestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;


@Slf4j
@Component
public class SummarizeStep {

    public Mono<Void> execute(RunContext ctx, List<IngestUniverseBarsStep.IngestResult> results) {
        long ok = results.stream().filter(IngestUniverseBarsStep.IngestResult::success).count();
        long fail = results.size() - ok;

        if (fail == 0) {
            log.info("[RUN] summary ok={} fail=0 market={} intervalCd={} runId={}",
                    ok, ctx.market(), ctx.intervalCd(), ctx.runId());
            return Mono.empty();
        }

        var failedIds = results.stream()
                .filter(r -> !r.success())
                .map(IngestUniverseBarsStep.IngestResult::universeId)
                .toList();

        log.warn("[RUN] summary ok={} fail={} failedUniverseIds={} market={} intervalCd={} runId={}",
                ok, fail, failedIds, ctx.market(), ctx.intervalCd(), ctx.runId());

        return Mono.empty();
    }
}