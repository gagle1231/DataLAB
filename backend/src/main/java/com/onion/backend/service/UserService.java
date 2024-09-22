package com.onion.backend.service;

import com.onion.backend.dto.request.SignupRequest;
import com.onion.backend.entity.User;
import com.onion.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(SignupRequest signupUser) {
        if (userRepository.findByEmail(signupUser.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }
        String encryptedPassword = passwordEncoder.encode(signupUser.password());

        User newUser = User.builder()
                .email(signupUser.email())
                .username(signupUser.username())
                .password(encryptedPassword)
                .build();

        return userRepository.save(newUser);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
