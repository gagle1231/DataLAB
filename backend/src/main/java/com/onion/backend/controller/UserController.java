package com.onion.backend.controller;

import com.onion.backend.entity.User;
import com.onion.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 생성
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided username, password, and email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        ResponseEntity<User> result;
        try {
            User newUser = userService.createUser(username, password, email);
            result = ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(newUser);
        } catch (IllegalArgumentException e) {
            result = ResponseEntity.badRequest().build();
        }
        return result;
    }

    // 사용자 삭제
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
