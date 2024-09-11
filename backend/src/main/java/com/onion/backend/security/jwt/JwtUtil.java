package com.onion.backend.security.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    private static String AUTHORIZATION_HEADER = "Authorization";
    private static String AUTHORIZATION_TOKEN_TYPE = "Bearer ";

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

    // JWT 토큰에서 만료 시간을 추출하는 메소드
    public Date getExpiryDateFromToken(String token) {
        return getClaims(token).getExpiration();
    }

    public String getToken(HttpServletRequest request) {
        // 1. 헤더에서 토큰 가져오기
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 헤더에 토큰이 없으면 쿠키에서 토큰 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> jwtCookie = Arrays.stream(cookies)
                    .filter(cookie -> "JWT-TOKEN".equals(cookie.getName()))
                    .findFirst();

            if (jwtCookie.isPresent()) {
                return jwtCookie.get().getValue();
            }
        }

        // 3. 헤더와 쿠키 둘 다 없다면 null 반환
        return null;
    }
}
