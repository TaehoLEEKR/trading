package com.trade.common.filter;

import com.trade.common.component.JwtProvider;
import com.trade.common.constant.ErrorCode;
import com.trade.common.response.ApiResponse;
import com.trade.common.util.JsonUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class JwtAuthFilter implements Filter {

    private final JwtProvider jwtProvider;
    private final List<String> whitelistPrefix;

    public JwtAuthFilter(JwtProvider jwtProvider, List<String> whitelistPrefix) {
        this.jwtProvider = jwtProvider;
        this.whitelistPrefix = whitelistPrefix == null ? List.of() : whitelistPrefix;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest req) || !(response instanceof HttpServletResponse res)) {
            chain.doFilter(request, response);
            return;
        }

        String path = req.getRequestURI();
        if (isWhitelisted(path)) {
            chain.doFilter(request, response);
            return;
        }

        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            log.warn("Missing Authorization header");
            writeError(res, ErrorCode.UNAUTHORIZED);
            return;
        }

        String token = auth.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            log.warn("Empty token");
            writeError(res, ErrorCode.UNAUTHORIZED);
            return;
        }

        try {
            jwtProvider.parseAccessToken(token);

            req.setAttribute("userId", jwtProvider.getUserId(token));
            req.setAttribute("role", jwtProvider.getRole(token));

            chain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid token", e);
            writeError(res, ErrorCode.UNAUTHORIZED);
        }
    }

    private boolean isWhitelisted(String path) {
        if (path == null) return true;

        for (String prefix : whitelistPrefix) {
            if (prefix == null || prefix.isBlank()) continue;

            if (path.equals(prefix)) return true;

            if (prefix.endsWith("/")) {
                if (path.startsWith(prefix)) return true;
                continue;
            }

            if (path.startsWith(prefix) && (path.length() == prefix.length() || path.charAt(prefix.length()) == '/')) {
                return true;
            }
        }
        return false;
    }

    private void writeError(HttpServletResponse res, ErrorCode errorCode) throws IOException {
        res.setStatus(errorCode.getHttpStatus().value());
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.setContentType("application/json; charset=utf-8");

        ApiResponse<Void> body = ApiResponse.error(errorCode);
        String json = JsonUtil.getInstance().encodeToJson(body);
        res.getWriter().write(json);
    }
}