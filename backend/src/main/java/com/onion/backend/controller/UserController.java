package com.onion.backend.controller;

import com.onion.backend.dto.SignupRequest;
import com.onion.backend.entity.User;
import com.onion.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Registers a new user with the provided username, password, and email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<User> registerUser(@RequestBody SignupRequest signupUser) {
        ResponseEntity<User> result;
        try {
            User newUser = userService.createUser(signupUser);
            result = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        } catch (IllegalArgumentException e) {
            result = ResponseEntity.badRequest().build();
        }
        return result;
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by the given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
