package com.example.taskapi.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username
) {}
