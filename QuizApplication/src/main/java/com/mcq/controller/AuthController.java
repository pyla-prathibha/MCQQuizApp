package com.mcq.controller;

import com.mcq.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")

@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        boolean isAuthenticated = authService.authenticate(username, password);
        if (isAuthenticated) {
            return ResponseEntity.ok(Map.of("message", "Login successful!"));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials!"));
        }
    }
}
