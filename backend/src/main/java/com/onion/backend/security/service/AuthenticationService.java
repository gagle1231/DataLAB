package com.onion.backend.security.service;

import com.onion.backend.repository.TokenBlacklistRepository;
import com.onion.backend.security.jwt.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public String login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails.getUsername());
        LocalDateTime expiryDate = jwtUtil.getExpiryDateFromToken(token)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        tokenBlacklistService.addTokenToBlacklist(token, expiryDate);
        return token;
    }

    public boolean validateToken(String token){
        return jwtUtil.validateToken(token);
    }
}
