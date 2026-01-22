package com.trade.md.kis;

import com.trade.common.component.KisTokenStore;
import com.trade.common.config.KisAuthConfig;
import com.trade.common.constant.staticConst;
import com.trade.common.model.token.TokenResponse;
import com.trade.common.util.CallClient;
import com.trade.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static com.trade.common.constant.staticConst.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class KisAccessTokenManager {

    private final KisTokenStore kisTokenStore;
    private final CallClient callClient;
    private final KisAuthConfig kisAuthConfig;

    private final ReentrantLock lock = new ReentrantLock();

    public String getOrIssue() {
        TokenResponse cached = kisTokenStore.getAccessToken();

        if (cached != null && cached.getAccessToken() != null && !cached.getAccessToken().isBlank()) {
            return cached.getAccessToken();
        }

        lock.lock();
        try {
            TokenResponse cached2 = kisTokenStore.getAccessToken();
            if (cached2 != null && cached2.getAccessToken() != null && !cached2.getAccessToken().isBlank()) {
                return cached2.getAccessToken();
            }
            TokenResponse issued = issueDirectly();
            kisTokenStore.storeAccessToken(issued);
            return issued.getAccessToken();
        } finally {
            lock.unlock();
        }
    }

    // kis 에서 EGW00121/00123 등 토큰 문제면 1회 강제갱신
    public String forceRefresh() {
        lock.lock();
        try {
            TokenResponse issued = issueDirectly();
            kisTokenStore.storeAccessToken(issued);
            return issued.getAccessToken();
        } finally {
            lock.unlock();
        }
    }

    private TokenResponse issueDirectly() {
        // KIS 토큰 발급
        String url = kisAuthConfig.token().authDomain() + kisAuthConfig.token().authDomainUrl();

        log.info("callRequireTokenUrl : {}",url);
        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("grant_type","client_credentials");
        paramMap.put("appkey",kisAuthConfig.key().app());
        paramMap.put("appsecret",kisAuthConfig.key().secret());

        String responseToken = callClient.POST(url,headers,paramMap);
        log.info("responseToken : {}",responseToken);

        TokenResponse tokenResponse = JsonUtil.getInstance().decodeFromJson(responseToken,TokenResponse.class);
//        kisTokenStore.storeAccessToken(tokenResponse);
        return tokenResponse;

    }

}
