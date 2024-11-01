package com.onion.backend.controller;

import com.onion.backend.dto.request.LoginRequest;
import com.onion.backend.security.service.AuthenticationService;
import com.onion.backend.security.service.TokenBlacklistService;
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
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(summary = "Login", description = "Login with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sign in successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user not exists")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        String token = authenticationService.login(request.email(), request.password());
        // JWT를 쿠키에 저장
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 작동하게 설정 (HTTPS 사용 시)
        cookie.setPath("/"); // 쿠키의 경로 설정
        cookie.setMaxAge(60 * 60); // 쿠키 만료 시간을 1시간으로 설정
        response.addCookie(cookie);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "jwt_token", required = false) String tokenFromCookie,
                                         @RequestParam(value = "token", required = false) String tokenFromRequest,
                                         HttpServletResponse response) {
        String token = tokenFromCookie != null ? tokenFromCookie : tokenFromRequest;
        if(token == null){
            return ResponseEntity.badRequest().body("No token provided");
        }
        // token을 블랙리스트에 추가
        tokenBlacklistService.addTokenToBlacklist(token);

        Cookie cookie = new Cookie("jwt_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
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
