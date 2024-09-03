package com.onion.backend.controller;

import com.onion.backend.dto.LoginRequest;
import com.onion.backend.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    @Operation(summary = "Login", description = "Login with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sign in successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not exists")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String token = authenticationService.login(request.email(), request.password());
        // JWT를 쿠키에 저장
        Cookie cookie = new Cookie("JWT", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 작동하게 설정 (HTTPS 사용 시)
        cookie.setPath("/"); // 쿠키의 경로 설정
        cookie.setMaxAge(60 * 60); // 쿠키 만료 시간을 1시간으로 설정
        response.addCookie(cookie);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // JWT 쿠키 삭제
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); // 쿠키의 경로 설정
        cookie.setMaxAge(0); // 쿠키 만료 시간을 0으로 설정하여 삭제
        response.addCookie(cookie);

        return ResponseEntity.ok("Successfully logged out");
    }

    @PostMapping("/token/validation")
    @ResponseStatus(OK)
    public void jwtValidate(@RequestParam String token){
        if(!authenticationService.validateToken(token)){
            throw new ResponseStatusException(FORBIDDEN);
        }

    }
}
