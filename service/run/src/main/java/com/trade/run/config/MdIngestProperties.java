package com.trade.run.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "run.md")
public record MdIngestProperties(
        String baseUrl,
        String cron
) {
}