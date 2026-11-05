package com.platform.auth.service;

import com.platform.auth.dto.AuthResponse;
import com.platform.auth.dto.LoginRequest;
import com.platform.auth.dto.RegisterRequest;
import com.platform.auth.dto.UserResponse;
import com.platform.auth.model.User;
import com.platform.auth.repository.RoleRepository;
import com.platform.auth.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String USER_NOT_FOUND = "User not found";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuditService auditService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        var role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        if (request.role() != null) {
            var requested = roleRepository.findByName(request.role().toUpperCase()).orElse(null);
            if (requested == null) {
                throw new IllegalArgumentException("Invalid role: " + request.role());
            }
            if (requested.getName().equals("ADMIN")) {
                throw new IllegalArgumentException("Cannot register as ADMIN");
            }
            role = requested;
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(role)
                .build();

        user = userRepository.save(user);
        String token = generateToken(user);

        auditService.log(user.getId(), user.getEmail(), "REGISTER",
                user.getId(), "USER", "Registered as " + role.getName());

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = generateToken(user);

        auditService.log(user.getId(), user.getEmail(), "LOGIN",
                user.getId(), "USER", "Logged in");

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole().getName(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    public UserResponse updateUserRole(String id, String newRoleName, String actorId, String actorEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        if (newRoleName == null) {
            throw new RuntimeException("Role is required");
        }
        String oldRole = user.getRole().getName();
        var role = roleRepository.findByName(newRoleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found: " + newRoleName));
        user.setRole(role);
        UserResponse res = toUserResponse(userRepository.save(user));

        auditService.log(actorId, actorEmail, "UPDATE_ROLE",
                id, "USER", "Role changed from " + oldRole + " to " + newRoleName.toUpperCase());

        return res;
    }

    public UserResponse toggleUserEnabled(String id, boolean enabled, String actorId, String actorEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        user.setEnabled(enabled);
        UserResponse res = toUserResponse(userRepository.save(user));

        auditService.log(actorId, actorEmail, enabled ? "ENABLE_USER" : "DISABLE_USER",
                id, "USER", "User " + (enabled ? "enabled" : "disabled"));

        return res;
    }

    public void deleteUser(String id, String actorId, String actorEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        auditService.log(actorId, actorEmail, "DELETE_USER",
                id, "USER", "Deleted user " + user.getEmail());
        userRepository.delete(user);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getName(),
                user.isEnabled(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null
        );
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("email", user.getEmail());

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .subject(user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}