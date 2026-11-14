package com.platform.familytree.shared.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class FamilyTreeCorsConfig {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(FamilyTreeCorsConfig.class);
    @Bean
    public FilterRegistrationBean<CorsFilter> familyTreeCorsFilter(
            @Value("${FAMILY_TREE_CORS_ORIGIN:http://localhost:5173}") String allowedOrigin) {
        String effectiveOrigin = allowedOrigin == null ? "" : allowedOrigin.trim();

        log.info("Family Tree CORS allowed origin: [{}]", effectiveOrigin);

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(effectiveOrigin));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of(
                "Content-Type", "X-Beta-Passcode"
        ));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        FilterRegistrationBean<CorsFilter> registration =
                new FilterRegistrationBean<>(new CorsFilter(source));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }
}
