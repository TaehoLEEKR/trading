package com.trade.common.component;

import com.trade.common.model.token.SocketResponse;
import com.trade.common.model.token.TokenResponse;
import com.trade.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class KisTokenStore {

    private static final String ACCESS_TOKEN_KEY = "kis:accessToken";
    private static final String ACCESS_SOCKET_TOKEN_KEY = "kis:socket:accessToken";
    private static final String ACCESS_TOKEN_LOCK_KEY = "kis:accessToken:lock";
    private static final long ACCESS_TOKEN_MAX_TTL_SECONDS = 21600L;

    @Qualifier("redisTemplate")
    private final StringRedisTemplate redis;

    public TokenResponse getAccessToken() {
        String raw = redis.opsForValue().get(ACCESS_TOKEN_KEY);
        if (raw == null) {
            return null;
        }
        return JsonUtil.getInstance().decodeFromJson(raw, TokenResponse.class);
    }

    public SocketResponse getSocketToken() {
        String raw = redis.opsForValue().get(ACCESS_SOCKET_TOKEN_KEY);
        if (raw == null) {
            return null;
        }
        return JsonUtil.getInstance().decodeFromJson(raw, SocketResponse.class);
    }

    public void storeAccessToken(TokenResponse tokenResponse) {
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            return;
        }
        String serialized = JsonUtil.getInstance().encodeToJson(tokenResponse);
        Integer expiresIn = tokenResponse.getExpiresIn();
        if (expiresIn == null || expiresIn <= 0) {
            redis.opsForValue().set(ACCESS_TOKEN_KEY, serialized);
            return;
        }
        long ttlSeconds = Math.min(expiresIn.longValue(), ACCESS_TOKEN_MAX_TTL_SECONDS);
        redis.opsForValue().set(ACCESS_TOKEN_KEY, serialized, Duration.ofSeconds(ttlSeconds));
    }

    public boolean tryAcquireAccessTokenLock(Duration ttl) {
        Boolean success = redis.opsForValue().setIfAbsent(ACCESS_TOKEN_LOCK_KEY, "1", ttl);
        return Boolean.TRUE.equals(success);
    }

    public void releaseAccessTokenLock() {
        redis.delete(ACCESS_TOKEN_LOCK_KEY);
    }

    public void deleteAccessToken() {
        redis.delete(ACCESS_TOKEN_KEY);
    }
}