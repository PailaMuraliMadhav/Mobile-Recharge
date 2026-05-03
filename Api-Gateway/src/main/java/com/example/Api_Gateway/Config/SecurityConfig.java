package com.example.Api_Gateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: SecurityConfig
 * DESCRIPTION:
 *   Spring Security configuration for the reactive API Gateway.
 *   Disables CSRF and form login, configures CORS with explicit allowed origins,
 *   and permits all exchanges (JWT enforcement is handled by JwtAuthenticationFilter).
 */
@Configuration
public class SecurityConfig {

    /* ================================================================
     * METHOD: securityWebFilterChain
     * DESCRIPTION:
     *   Defines the reactive security filter chain. Disables CSRF and HTTP Basic auth,
     *   applies the CORS configuration, and permits all routes since JWT validation
     *   is delegated to the custom global filter.
     * ================================================================ */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/fallback/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyExchange().permitAll()
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    /* ================================================================
     * METHOD: corsConfigurationSource
     * DESCRIPTION:
     *   Builds and registers a CORS configuration that allows requests from the Angular
     *   frontend origins, supporting standard HTTP methods and all headers with credentials.
     * ================================================================ */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Explicit origins — wildcard + allowCredentials=true causes duplicate header issues
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:80",
                "http://frontend",
                "http://frontend:80"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
