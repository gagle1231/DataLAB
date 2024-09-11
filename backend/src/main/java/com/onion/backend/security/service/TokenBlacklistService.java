package com.onion.backend.security.service;

import com.onion.backend.entity.TokenBlacklist;
import com.onion.backend.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public void addTokenToBlacklist(String token, LocalDateTime expiryDate) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist(token, expiryDate);
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        Optional<TokenBlacklist> tokenBlacklist = tokenBlacklistRepository.findByToken(token);
        return !tokenBlacklist.isPresent();
    }

    public void removeExpiredTokens() {
        tokenBlacklistRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }
}
