package com.example.aiintegration.controller;

import com.example.aiintegration.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * Provides login endpoint to obtain JWT token.
 * Fixes OWASP A07: Identification & Authentication Failures
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Simple login endpoint - generates JWT token
     * In production, validate username/password against a database
     *
     * @param username username
     * @param password password (not validated in this simple setup)
     * @return JWT token in response
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String username,
            @RequestParam String password) {

        // Simple validation - in production, query database
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(username);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("username", username);
        response.put("message", "Login successful. Use this token with Authorization: Bearer <token> header");

        return ResponseEntity.ok(response);
    }
}
