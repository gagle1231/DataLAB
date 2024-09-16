package com.onion.backend.security.service;

import com.onion.backend.entity.TokenBlacklist;
import com.onion.backend.repository.TokenBlacklistRepository;
import com.onion.backend.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtUtil jwtUtil;

    public void addTokenToBlacklist(String token) {
        String email = jwtUtil.extractEmail(token);
        LocalDateTime expiryDate = dateToLocalDateTime(jwtUtil.getExpiryDateFromToken(token));

        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                .email(email)
                .token(token)
                .expiryDate(expiryDate)
                .build();

        tokenBlacklistRepository.save(tokenBlacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        Optional<TokenBlacklist> tokenBlacklist = tokenBlacklistRepository.findByToken(token);
        return tokenBlacklist.isPresent();
    }

    public void removeExpiredTokens() {
        tokenBlacklistRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }

    public LocalDateTime dateToLocalDateTime(Date date){
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
