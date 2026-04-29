package com.example.taskapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> login() {
        return ResponseEntity.ok(Map.of(
            "message", "Please login via Keycloak",
            "url", "http://localhost:8180/realms/task-api/protocol/openid-connect/token"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me() {
        return ResponseEntity.ok(Map.of(
            "username", "authenticated-user",
            "message", "Health check endpoint"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}