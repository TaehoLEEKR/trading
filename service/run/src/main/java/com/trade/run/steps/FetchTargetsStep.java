package com.trade.run.steps;

import com.trade.run.client.CatalogClient;
import com.trade.run.model.UniverseTargetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FetchTargetsStep {

    private final CatalogClient catalogClient;

    public Mono<List<UniverseTargetDto.UniverseTargetsResponse.Target>> execute(RunContext ctx) {
        return catalogClient.getTargets(ctx.intervalCd(), ctx.market(), ctx.limit())
                .map(res -> res.targets() == null ? List.of() : res.targets());
    }
}
