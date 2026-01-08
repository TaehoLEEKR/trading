package com.trade.common.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private final String secret;
    private final long accessTokenValiditySeconds;
    private final String issuer;
    private SecretKey secretKey;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-seconds:900}") long accessTokenValiditySeconds,
            @Value("${jwt.issuer:trading}") String issuer
    ) {
        this.secret = secret;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.issuer = issuer;
    }

    @PostConstruct
    void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt ENV 확인 필요");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT ENV BYTE 32 길이 필요");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String issueAccessToken(String userId, String role) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plusSeconds(accessTokenValiditySeconds));

        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userId)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .claim("role", role)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserId(String token) {
        return parseAccessToken(token).getSubject();
    }

    public String getRole(String token) {
        Object role = parseAccessToken(token).get("role");
        return role == null ? null : role.toString();
    }


}
