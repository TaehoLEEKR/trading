package com.trade.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "internal.auth")
public record InternalAuthProperties(
        boolean enabled,
        String token,
        List<String> protectedPaths
) {}