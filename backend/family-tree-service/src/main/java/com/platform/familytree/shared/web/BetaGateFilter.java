package com.platform.familytree.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Shared-passcode gate for the private family beta.
 *
 * <p>When {@code beta.passcode} is set, every {@code /api/**} request must carry it in the
 * {@code X-Beta-Passcode} header. When it is empty (local development) the gate is off.
 * Static files stay public so the passcode screen itself can load.</p>
 */
@Component
public class BetaGateFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Beta-Passcode";

    private final byte[] expected;

    public BetaGateFilter(@Value("${beta.passcode:}") String passcode) {
        this.expected = passcode == null
                ? new byte[0]
                : passcode.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (expected.length == 0 || !request.getRequestURI().startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String supplied = request.getHeader(HEADER);
        if (supplied != null
                && MessageDigest.isEqual(expected, supplied.getBytes(StandardCharsets.UTF_8))) {
            chain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(
                "{\"code\":\"BETA_REQUIRED\",\"message\":\"A family passcode is required.\"}");
    }
}
