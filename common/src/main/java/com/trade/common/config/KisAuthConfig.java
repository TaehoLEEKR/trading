package com.trade.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trade.kis")
public record KisAuthConfig(
        Token token,
        Socket socket
) {
    public record Token(
            String authDomainUrl,
            String authDomain,
            String authDisposalUrl,
            String authDisposalDomain
    ) {}
    public record Socket(
            String url,
            String domain
    ) {}

}
