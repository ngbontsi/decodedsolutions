package com.platform.familytree.shared.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FamilyTreeCorsConfig implements WebMvcConfigurer {

    private final String allowedOrigin;

    public FamilyTreeCorsConfig(
            @Value("${FAMILY_TREE_CORS_ORIGIN:http://localhost:5173}")
            String allowedOrigin) {
        this.allowedOrigin = allowedOrigin.trim();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods(
                        "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
                )
                .allowedHeaders("Content-Type", "X-Beta-Passcode")
                .allowCredentials(false)
                .maxAge(3600);
    }
}