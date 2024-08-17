package com.onion.backend.repository;

import com.onion.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름으로 사용자 조회
    Optional<User> findByUsername(String username);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);
}
