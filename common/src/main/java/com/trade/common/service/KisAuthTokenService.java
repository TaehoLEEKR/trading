package com.trade.common.service;

import com.trade.common.component.KisTokenStore;
import com.trade.common.config.KisAuthConfig;
import com.trade.common.model.token.SocketResponse;
import com.trade.common.model.token.TokenResponse;
import com.trade.common.response.CodeResponse;
import com.trade.common.util.CallClient;
import com.trade.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.trade.common.constant.staticConst.headers;

@Service
@RequiredArgsConstructor
@Slf4j
public class KisAuthTokenService {

    // 레디스 락
    private static final Duration ACCESS_TOKEN_LOCK_TTL = Duration.ofSeconds(5);
    private static final int ACCESS_TOKEN_WAIT_RETRIES = 3;
    private static final long ACCESS_TOKEN_WAIT_MILLIS = 100L;

    private final KisAuthConfig kisAuthConfig;
    private final CallClient callClient;
    private final KisTokenStore kisTokenStore;

    public TokenResponse getRequiredToken(){

        TokenResponse cachedToken = kisTokenStore.getAccessToken();
        if (cachedToken != null && cachedToken.getAccessToken() != null) {
            return cachedToken;
        }

        if (!kisTokenStore.tryAcquireAccessTokenLock(ACCESS_TOKEN_LOCK_TTL)) {
            return waitForCachedToken();
        }

        try {
            TokenResponse retryCachedToken = kisTokenStore.getAccessToken();
            if (retryCachedToken != null && retryCachedToken.getAccessToken() != null) {
                return retryCachedToken;
            }

            String callRequireTokenUrl = kisAuthConfig.token().authDomain() + kisAuthConfig.token().authDomainUrl();
            log.info("callRequireTokenUrl : {}",callRequireTokenUrl);
            Map<String,Object> paramMap = new HashMap<>();

            paramMap.put("grant_type","client_credentials");
            paramMap.put("appkey",kisAuthConfig.key().app());
            paramMap.put("appsecret",kisAuthConfig.key().secret());

            String responseToken = callClient.POST(callRequireTokenUrl,headers,paramMap);
            log.info("responseToken : {}",responseToken);

            TokenResponse tokenResponse = JsonUtil.getInstance().decodeFromJson(responseToken,TokenResponse.class);
            kisTokenStore.storeAccessToken(tokenResponse);
            return tokenResponse;
        } finally {
            kisTokenStore.releaseAccessTokenLock();
        }
    }

    public CodeResponse TokenExpired(Map<String, String> accessToken){
        Map<String,Object> paramMap = new HashMap<>();

        String callRequireTokenExpireUrl = kisAuthConfig.token().authDisposalDomain() + kisAuthConfig.token().authDisposalUrl();

        log.info("callRequireTokenExpireUrl : {}",callRequireTokenExpireUrl);
        log.info("accessToken : {}",accessToken);

        paramMap.put("token",accessToken.get("accessToken"));
        paramMap.put("appkey",kisAuthConfig.key().app());
        paramMap.put("appsecret",kisAuthConfig.key().secret());

        String responseExpireResponse = callClient.POST(callRequireTokenExpireUrl,headers,paramMap);
        log.info("responseExpireResponse : {}",responseExpireResponse);

        CodeResponse result = JsonUtil.getInstance().decodeFromJson(responseExpireResponse,CodeResponse.class);
        if ("200".equals(result.getCode())) {
            kisTokenStore.deleteAccessToken();
        }
        return result;
    }

    public SocketResponse getSocketToken() {

        SocketResponse cachedToken = kisTokenStore.getSocketToken();
        if (cachedToken != null && cachedToken.getApprovalKey() != null) {
            return cachedToken;
        }

        String callRequireSocketUrl = kisAuthConfig.socket().domain()+ kisAuthConfig.socket().url();

        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("grant_type","client_credentials");
        paramMap.put("appkey",kisAuthConfig.key().app());
        paramMap.put("appsecret",kisAuthConfig.key().secret());

        String responseToken = callClient.POST(callRequireSocketUrl,headers,paramMap);
        log.info("responseSocketToken : {}",responseToken);

        return JsonUtil.getInstance().decodeFromJson(responseToken,SocketResponse.class);
    }

    private TokenResponse waitForCachedToken() {
        for (int i = 0; i < ACCESS_TOKEN_WAIT_RETRIES; i++) {
            try {
                Thread.sleep(ACCESS_TOKEN_WAIT_MILLIS);
            } catch (InterruptedException e) {
                log.error("Failed to wait for cached token: {}", e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
            TokenResponse cachedToken = kisTokenStore.getAccessToken();
            if (cachedToken != null && cachedToken.getAccessToken() != null) {
                return cachedToken;
            }
        }
        return null;
    }
}
