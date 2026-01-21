package com.trade.run.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MdClient {

    private final WebClient webClient;

    public MdClient(@Qualifier("mdWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
    public record UniverseIngestBarsRequest(
            String universeId,
            String intervalCd,
            String market,
            Integer limit,
            Integer maxInstruments
    ) {}

    public Mono<Void> ingestBarsByUniverse(UniverseIngestBarsRequest req) {
        return webClient.post()
                .uri("/v1/api/md/universe/ingest/bars")
                .bodyValue(req)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        resp -> resp.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> Mono.error(new RuntimeException(
                                        "MD ingest failed status=" + resp.statusCode() + " body=" + body
                                )))
                )
                .toBodilessEntity()
                .then();
    }
}
