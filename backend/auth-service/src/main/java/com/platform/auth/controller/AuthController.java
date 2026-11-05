package com.platform.auth.controller;

import com.platform.auth.dto.AuthResponse;
import com.platform.auth.dto.LoginRequest;
import com.platform.auth.dto.RegisterRequest;
import com.platform.auth.dto.UserResponse;
import com.platform.auth.repository.UserRepository;
import com.platform.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth and user management endpoints")
public class AuthController {

    public static final String ADMIN = "ADMIN";
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists",
                    content = @Content)
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content)
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieves all platform users (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)))
    })
    public ResponseEntity<java.util.List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role", description = "Updates a user's role (admin only)")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String callerId,
            @RequestHeader("X-User-Email") String callerEmail) {
        if (!isAdmin(callerId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(authService.updateUserRole(id, body.get("role"), callerId, callerEmail));
    }

    @PutMapping("/users/{id}/enabled")
    @Operation(summary = "Toggle user enabled", description = "Enables or disables a user account (admin only)")
    public ResponseEntity<UserResponse> toggleUserEnabled(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> body,
            @RequestHeader("X-User-Id") String callerId,
            @RequestHeader("X-User-Email") String callerEmail) {
        if (!isAdmin(callerId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(authService.toggleUserEnabled(id, body.get("enabled"), callerId, callerEmail));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user account (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin only")
    })
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String callerId,
            @RequestHeader("X-User-Email") String callerEmail) {
        if (!isAdmin(callerId)) {
            return ResponseEntity.status(403).build();
        }
        authService.deleteUser(id, callerId, callerEmail);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(String callerId){
        if(callerId == null)return  false;
        return userRepository.findById(callerId)
                .map(user -> user.getRole() != null && ADMIN.equalsIgnoreCase(user.getRole().getName()))
                .orElse(false);
    }
}
