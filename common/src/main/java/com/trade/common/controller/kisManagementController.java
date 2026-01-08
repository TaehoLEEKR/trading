package com.trade.common.controller;

import com.trade.common.config.KisAuthConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kis/auth")
@Slf4j
@RequiredArgsConstructor
public class kisManagementController {
    private final KisAuthConfig kisAuthConfig;

    
}
