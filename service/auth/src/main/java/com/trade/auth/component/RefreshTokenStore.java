package com.trade.auth.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RefreshTokenStore {

    @Qualifier("redisTemplate")
    private final StringRedisTemplate redis;

    public void store(String refreshToken, String userId, Duration ttl) {
        String hash = sha256(refreshToken);
        String key = "rt:" + hash;

        redis.opsForHash().put(key, "userId", userId);
        redis.opsForHash().put(key, "issuedAt", String.valueOf(System.currentTimeMillis()));
        redis.expire(key, ttl);
    }

    public String getUserId(String refreshToken) {
        String hash = sha256(refreshToken);
        Object v = redis.opsForHash().get("rt:" + hash, "userId");
        return v == null ? null : v.toString();
    }

    public void revoke(String refreshToken) {
        String hash = sha256(refreshToken);
        redis.delete("rt:" + hash);
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
