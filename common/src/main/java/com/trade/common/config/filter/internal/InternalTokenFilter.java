package com.trade.common.config.filter.internal;

import com.trade.common.config.InternalAuthProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RequiredArgsConstructor
@Slf4j
public class InternalTokenFilter extends OncePerRequestFilter {

    private final InternalAuthProperties props;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!props.enabled()) return true;

        String path = request.getRequestURI();
        var paths = props.protectedPaths();
        if (paths == null || paths.isEmpty()) return true;

        for (String p : paths) {
            if (matcher.match(p, path)) return false; // 이 요청은 검사해야 함
        }
        return true; // 보호 대상 아님
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String expected = props.token();
        String provided = req.getHeader("X-Internal-Token");

        if (expected == null || expected.isBlank()) {
            res.sendError(500, "Internal token is not configured");
            log.error("Internal token is not configured");
            return;
        }
        if (provided == null || provided.isBlank()) {
            res.sendError(401, "Missing X-Internal-Token");
            log.error("401 Missing X-Internal-Token");
            return;
        }

        boolean ok = MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8)
        );
        if (!ok) {
            res.sendError(401, "Invalid X-Internal-Token");
            log.error("401 Invalid X-Internal-Token");
            return;
        }

        chain.doFilter(req, res);
    }
}
