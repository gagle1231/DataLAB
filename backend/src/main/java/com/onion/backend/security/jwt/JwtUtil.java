package com.onion.backend.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    // JWT 토큰 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT 토큰에서 이메일 추출
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            // 로그 기록 또는 예외 처리 로직을 추가할 수 있음
            return false;
        }
    }

    public boolean validateToken(String token, String email) {
        return (email.equals(extractEmail(token)) && !isTokenExpired(token));
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // 토큰에서 클레임 추출
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            throw new JwtException("Token is expired", e);
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
            throw new JwtException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            // 잘못된 JWT 토큰
            throw new JwtException("Malformed JWT token", e);
        } catch (SignatureException e) {
            // JWT 서명 오류
            throw new JwtException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            // 비어있는 토큰 등
            throw new JwtException("JWT token is not valid", e);
        }
    }
}
