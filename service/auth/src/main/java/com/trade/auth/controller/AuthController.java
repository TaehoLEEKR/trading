package com.trade.auth.controller;

import com.trade.auth.model.SignupDto;
import com.trade.auth.service.SignupService;
import com.trade.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/api/auth")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final SignupService signupService;

    @PostMapping("/signup")
    public ApiResponse<SignupDto.Response> signup(@Validated @RequestBody SignupDto.Request request) {
        return ApiResponse.success(signupService.signup(request));
    }
}
