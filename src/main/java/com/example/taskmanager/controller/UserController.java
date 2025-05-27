package com.example.taskmanager.controller;

import com.example.taskmanager.model.User;
import com.example.taskmanager.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRequest {
        private String username;
        private String password;
    }

    // Login endpoint (GET with username and password as query params for simplicity)
    @GetMapping("/login")
    public ResponseEntity<User> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> userOpt = userService.login(username, password);
        return userOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    // Registration endpoint
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRequest userRequest) {
        if (userRequest.getUsername() == null || userRequest.getUsername().isBlank()
                || userRequest.getPassword() == null || userRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            User userToCreate = User.builder()
                    .username(userRequest.getUsername())
                    .password(userRequest.getPassword())
                    .build();

            User createdUser = userService.registerUser(userToCreate);
            URI location = URI.create(String.format("/api/v1/users/%d", createdUser.getUserId()));
            return ResponseEntity.created(location).body(createdUser);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).build();  // Conflict for duplicate username
        }
    }
}

