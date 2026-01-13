package com.trade.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "local.cmc")
public record localCommunication(
        String retryKisToken
) {

}
