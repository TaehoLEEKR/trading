package com.trade.auth.controller;

import com.trade.auth.model.LoginDto;
import com.trade.auth.model.SignupDto;
import com.trade.auth.record.LoginResult;
import com.trade.auth.service.SignupService;
import com.trade.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequestMapping("/v1/api/auth")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ApiResponse<SignupDto.Response> signup(@Valid @RequestBody SignupDto.Request request) {
        return ApiResponse.success(signupService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginDto.Response>> login(@Valid @RequestBody LoginDto.Request request) {

        LoginResult result = signupService.login(request);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", result.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(14))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success(result.body()));
    }

}
