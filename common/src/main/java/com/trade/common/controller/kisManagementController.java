package com.trade.common.controller;

import com.trade.common.config.KisAuthConfig;
import com.trade.common.constant.ErrorCode;
import com.trade.common.model.token.TokenResponse;
import com.trade.common.response.ApiResponse;
import com.trade.common.response.CodeResponse;
import com.trade.common.service.KisAuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/kis/auth")
@Slf4j
@RequiredArgsConstructor
public class kisManagementController {

    private final KisAuthTokenService kisAuthTokenService;

    @PostMapping("/oauth/token")
    public ApiResponse<TokenResponse> getToken() {
        try {
            return ApiResponse.success(kisAuthTokenService.getRequiredToken());
        }catch (Exception e){
            log.error("Failed to get token: {}", e.getMessage());
            return ApiResponse.error(ErrorCode.SERVER_ERROR);
        }
    }

    @PostMapping("/oauth/token/expired")
    public ApiResponse<?> TokenExpired(@RequestBody Map<String,String> accessToken){
        try {
            CodeResponse result = kisAuthTokenService.TokenExpired(accessToken);

            if (result.getCode().equals("200")) {
                return ApiResponse.success(result);
            } else {
                return ApiResponse.error(ErrorCode.TOKEN_DISPOSAL_ERROR, result.getMessage());
            }
        }catch (Exception e){
            log.error("Failed to expire token: {}", e.getMessage());
            return ApiResponse.error(ErrorCode.SERVER_ERROR);
        }
    }
}
