package com.platform.restaurant.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class UserContextFilter implements Filter {


    @Value("${internal.shared-secret:}")
    private String internalSharedSecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String provided = req.getHeader("X-Internal-Token");
        boolean trusted = internalSharedSecret!=null && !internalSharedSecret.isBlank()
                && internalSharedSecret.equals(provided);
        if (trusted){
            String userId = req.getHeader("X-User-Id");
            String userRole = req.getHeader("X-User-Role");
            String userEmail = req.getHeader("X-User-Email");

            if (userId != null) UserContext.setUserId(userId);
            if (userRole != null) UserContext.setUserRole(userRole);
            if (userEmail != null) UserContext.setUserEmail(userEmail);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
