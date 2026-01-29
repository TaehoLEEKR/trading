package com.trade.run.steps;

import com.trade.run.client.MdClient;
import com.trade.run.model.IngestResult;
import com.trade.run.model.UniverseTargetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class IngestUniverseBarsStep {

    private final MdClient mdClient;

    public Mono<IngestResult> execute(RunContext ctx, UniverseTargetDto.UniverseTargetsResponse.Target t) {
        int effectiveMax = (t.maxInstruments() != null) ? t.maxInstruments() : ctx.defaultMaxInstruments();

        var req = new MdClient.UniverseIngestBarsRequest(
                t.universeId(),
                t.intervalCd(),
                t.market(),
                ctx.limit(),
                effectiveMax
        );

        return mdClient.ingestBarsByUniverse(req)
                .thenReturn(new IngestResult(t.universeId(), true, null))
                .onErrorResume(e -> Mono.just(new IngestResult(t.universeId(), false, e.toString())));
    }

    public record IngestResult(String universeId, boolean success, String error) {}
}