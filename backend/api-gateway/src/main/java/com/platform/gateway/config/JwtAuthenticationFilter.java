package com.platform.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh"
    );

    private static final List<String> PUBLIC_GET_PREFIXES = List.of(
            "/products",
            "/categories",
            "/guesthouses",
            "/rooms"
    );
 private  static final String HEADER_USER_ID = "X-User-Id";
 private static final  String HEADER_USER_ROLE ="X-User-Role";
 private static  final String HEADER_USER_EMAIL ="X-User-Email";
 private static final String HEADER_INTERNAL ="X-Internal-Token";
 @Value("${internal.shared-secret:}")
 private String internalSharedSecret;
    private static final String LOGOUT_PATH = "/auth/logout";

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final TokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove(HEADER_USER_ID);
                    h.remove(HEADER_USER_ROLE);
                    h.remove(HEADER_USER_EMAIL);
                    h.remove(HEADER_INTERNAL);
                })
                .header(HEADER_INTERNAL,internalSharedSecret).build();
        ServerWebExchange cleanExchange = exchange.mutate().request(request).build();
        String path = request.getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(cleanExchange);
        }

        if (isPublicGetPath(path, request.getMethod().name())) {
            return chain.filter(cleanExchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(cleanExchange.getResponse());
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = validateToken(token);

            if (path.equals(LOGOUT_PATH)) {
                return handleLogout(token, claims, cleanExchange);
            }

            return blacklistService.isBlacklisted(token)
                    .flatMap(blacklisted -> {
                        if (Boolean.TRUE.equals(blacklisted))
                            return unauthorized(cleanExchange.getResponse());

                        String userId = claims.getSubject();
                        String role = (String) claims.get("role");
                        String email = (String) claims.get("email");

                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header(HEADER_USER_ID, userId)
                                .header(HEADER_USER_ROLE, role != null ? role : "USER")
                                .header(HEADER_USER_EMAIL, email != null ? email : "")
                                .build();

                        return chain.filter(cleanExchange.mutate().request(modifiedRequest).build());
                    });

        } catch (Exception e) {
            return unauthorized(cleanExchange.getResponse());
        }
    }

    private Mono<Void> handleLogout(String token, Claims claims, ServerWebExchange exchange) {
        Date expiration = claims.getExpiration();
        long ttlMillis = expiration.getTime() - System.currentTimeMillis();
        if (ttlMillis <= 0) {
            return ok(exchange.getResponse());
        }
        return blacklistService.blacklist(token, Duration.ofMillis(ttlMillis))
                .then(ok(exchange.getResponse()));
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isPublicGetPath(String path, String method) {
        if (!"GET".equalsIgnoreCase(method)) return false;
        return PUBLIC_GET_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private Mono<Void> ok(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.OK);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
