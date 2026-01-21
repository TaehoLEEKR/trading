package com.trade.run.client;

import com.trade.run.model.ApiResponse;
import com.trade.run.model.UniverseTargetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component

public class CatalogClient {

    private final WebClient webClient;

    public CatalogClient(@Qualifier("catalogWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    private static final ParameterizedTypeReference<ApiResponse<UniverseTargetDto.UniverseTargetsResponse>> RES_TYPE = new ParameterizedTypeReference<>() {};

    public Mono<UniverseTargetDto.UniverseTargetsResponse> getTargets(String intervalCd, String market, Integer limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/api/universes/internal/targets")
                        .queryParam("intervalCd", intervalCd)
                        .queryParam("market", market)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(RES_TYPE)
                .map(ApiResponse::data);
    }
}
