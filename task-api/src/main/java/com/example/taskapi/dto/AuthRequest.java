package com.example.taskapi.dto;

public record AuthRequest(
        String username,
        String password
) {}
