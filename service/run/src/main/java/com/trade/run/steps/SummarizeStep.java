package com.trade.run.steps;

import com.trade.run.model.IngestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class SummarizeStep {

    public Mono<Void> execute(RunContext ctx, List<IngestResult> results) {
        long ok = results.stream().filter(IngestResult::success).count();
        long fail = results.size() - ok;

        if (fail == 0) {
            log.info("[RUN] summary ok={} fail=0 market={} intervalCd={}", ok, ctx.market(), ctx.intervalCd());
            return Mono.empty();
        }

        var failedIds = results.stream()
                .filter(r -> !r.success())
                .map(IngestResult::universeId)
                .toList();

        log.warn("[RUN] summary ok={} fail={} failedUniverseIds={} market={} intervalCd={}",
                ok, fail, failedIds, ctx.market(), ctx.intervalCd());
        return Mono.empty();
    }
}
