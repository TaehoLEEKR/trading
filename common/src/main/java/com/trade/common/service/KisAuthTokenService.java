package com.trade.common.service;

import com.trade.common.config.KisAuthConfig;
import com.trade.common.model.token.SocketResponse;
import com.trade.common.model.token.TokenResponse;
import com.trade.common.response.CodeResponse;
import com.trade.common.util.CallClient;
import com.trade.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.trade.common.constant.staticConst.headers;

@Service
@RequiredArgsConstructor
@Slf4j
public class KisAuthTokenService {

    private final KisAuthConfig kisAuthConfig;
    private final CallClient callClient;

    public TokenResponse getRequiredToken(){

        String callRequireTokenUrl = kisAuthConfig.token().authDomain() + kisAuthConfig.token().authDomainUrl();
        log.info("callRequireTokenUrl : {}",callRequireTokenUrl);
        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("grant_type","client_credentials");
        paramMap.put("appkey",kisAuthConfig.key().app());
        paramMap.put("appsecret",kisAuthConfig.key().secret());

        String responseToken = callClient.POST(callRequireTokenUrl,headers,paramMap);
        log.info("responseToken : {}",responseToken);

        return JsonUtil.getInstance().decodeFromJson(responseToken,TokenResponse.class);
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

        return JsonUtil.getInstance().decodeFromJson(responseExpireResponse,CodeResponse.class);
    }

    public SocketResponse getSocketToken() {
        String callRequireSocketUrl = kisAuthConfig.socket().domain()+ kisAuthConfig.socket().url();

        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("grant_type","client_credentials");
        paramMap.put("appkey",kisAuthConfig.key().app());
        paramMap.put("appsecret",kisAuthConfig.key().secret());

        String responseToken = callClient.POST(callRequireSocketUrl,headers,paramMap);
        log.info("responseSocketToken : {}",responseToken);

        return JsonUtil.getInstance().decodeFromJson(responseToken,SocketResponse.class);
    }
}
