package com.onion.backend.controller;

import com.onion.backend.dto.LoginRequest;
import com.onion.backend.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @PostMapping("/signin")
    public ResponseEntity<String> login(@RequestBody LoginRequest user) {
        String token = authenticationService.login(user.email(), user.password());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token/validation")
    @ResponseStatus(OK)
    public void jwtValidate(@RequestParam String token){
        if(!authenticationService.validateToken(token)){
            throw new ResponseStatusException(FORBIDDEN);
        }

    }
}
