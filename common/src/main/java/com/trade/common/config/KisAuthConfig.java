package com.trade.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trade.kis")
public record KisAuthConfig(
        Token token,
        Socket socket,
        Key key,
        URL url
) {
    public record Key(String secret, String app) {}

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

    public record URL(
            String currentPrice,
            String inquireDailyPrice
    )
    {}
}
