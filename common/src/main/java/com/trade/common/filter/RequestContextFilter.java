package com.trade.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.trade.common.constant.staticConst.HEADER_REQUEST_ID;
import static com.trade.common.constant.staticConst.HEADER_RUN_ID;

@Component("tradeRequestContextFilter")
public class RequestContextFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {

        String requestId = request.getHeader(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }

        String runId = request.getHeader(HEADER_RUN_ID);

        MDC.put("requestId", requestId);
        if (runId != null && !runId.isBlank()) {
            MDC.put("runId", runId);
        }

        response.setHeader(HEADER_REQUEST_ID, requestId);
        if (runId != null && !runId.isBlank()) {
            response.setHeader(HEADER_RUN_ID, runId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
