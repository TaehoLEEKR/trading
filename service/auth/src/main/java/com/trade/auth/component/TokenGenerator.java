package com.trade.auth.component;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public String randomBase64Url(int byteLength) {
        if (byteLength <= 0) {
            throw new IllegalArgumentException("byteLength must be positive");
        }
        byte[] bytes = new byte[byteLength];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
