package com.trade.common.controller;

import com.trade.common.service.HealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
public class healthController {

    private final HealthService healthService;

    @GetMapping("/live")
    public String healthCheck(){
        // 헬스체크
        return LocalDateTime.now().toString();
    }

    @GetMapping("/ready")
    public ResponseEntity<String> ready() {
        return healthService.ping()
                ? ResponseEntity.ok("READY")
                : ResponseEntity.status(503).body("NOT_READY");
    }

}
