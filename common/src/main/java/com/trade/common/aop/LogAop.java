package com.trade.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LogAop {

    /**
     * common 모듈의 controller 패키지에만 적용
     */
    @Around(
            "(" +
                    "within(com.trade.common.controller..*) " +
                    "|| within(com.trade.common..controller..*)" +
                    ")" +
                    " && !within(com.trade.common..health..*)"
    )
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Signature sig = joinPoint.getSignature();
        String method = sig.toShortString();

        String argsSummary = summarizeArgs(joinPoint.getArgs());
        log.info("START - {} args={}", method, argsSummary);

        try {
            Object result = joinPoint.proceed();
            long tookMs = System.currentTimeMillis() - start;

            log.info("END - {} tookMs={} result={}", method, tookMs, summarizeResult(result));
            return result;
        } catch (Throwable t) {
            long tookMs = System.currentTimeMillis() - start;
            log.error("EXCEPTION - {} tookMs={} {}", method, tookMs, t.toString(), t);
            throw t;
        }
    }

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";

        return Arrays.stream(args)
                .map(this::safeSummary)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String summarizeResult(Object result) {
        if (result instanceof ResponseEntity<?> re) {
            Object body = re.getBody();
            return "ResponseEntity(status=" + re.getStatusCode().value() + ", body=" + safeSummary(body) + ")";
        }
        return safeSummary(result);
    }

    /**
     * toString() 호출로 인한 폭발(순환참조/대형객체/프록시) 방지:
     * - 단순 타입은 값 출력
     * - 나머지는 타입 + (가능하면) 짧은 toString, 실패 시 타입만
     */
    private String safeSummary(Object v) {
        if (v == null) return "null";

        if (v instanceof CharSequence cs) {
            return quoteAndLimit(cs.toString(), 2000);
        }
        if (v instanceof Number || v instanceof Boolean || v.getClass().isEnum()) {
            return String.valueOf(v);
        }

        // 컬렉션/맵/배열은 너무 커질 수 있어 타입만(원하면 size 정도는 추가 가능)
        if (v instanceof java.util.Collection<?> || v instanceof java.util.Map<?, ?> || v.getClass().isArray()) {
            return v.getClass().getName();
        }

        try {
            String s = String.valueOf(v);
            return v.getClass().getName() + "(" + limit(s, 2000) + ")";
        } catch (Exception e) {
            return v.getClass().getName();
        }
    }

    private String quoteAndLimit(String s, int max) {
        return "\"" + limit(s, max) + "\"";
    }

    private String limit(String s, int max) {
        if (s == null) return "null";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...(truncated)";
    }
}