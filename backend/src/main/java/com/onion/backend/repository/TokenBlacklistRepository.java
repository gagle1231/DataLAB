package com.onion.backend.repository;
import com.onion.backend.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenBlacklistRepository extends JpaRepository<com.onion.backend.entity.TokenBlacklist, Long> {
    Optional<TokenBlacklist> findByToken(String token);

    void deleteAllByExpiryDateBefore(LocalDateTime now);
}