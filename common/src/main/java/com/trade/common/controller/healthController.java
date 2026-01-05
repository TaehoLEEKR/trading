package com.trade.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
public class healthController {
    @GetMapping("/live")
    public String healthCheck(){
        // 헬스체크
        return LocalDateTime.now().toString();
    }

    @GetMapping("/ready")
    public String readyCheck(){
        // 추후 DB 체크
        return "ready";
    }

}
