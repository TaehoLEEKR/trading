package com.trade.run.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "run")
public record RunProperties(
        Internal internal,
        Md md,
        Catalog catalog
) {
    public record Internal(String token) {}

    public record Md(
            String baseUrl,
            String cron,
            String intervalCd,
            String market,
            Integer limit,
            Integer maxInstruments,
            Integer concurrency
    ) {}

    public record Catalog(String baseUrl) {}
}

