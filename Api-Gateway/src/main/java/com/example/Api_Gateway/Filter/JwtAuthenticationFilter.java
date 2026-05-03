package com.example.Api_Gateway.Filter;

import com.example.Api_Gateway.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: JwtAuthenticationFilter
 * DESCRIPTION:
 *   Global reactive gateway filter that enforces JWT authentication on all incoming requests.
 *   Skips validation for public endpoints (auth, Swagger, fallback) and OPTIONS preflight requests,
 *   and rejects requests with missing or invalid tokens with HTTP 401.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    /* ================================================================
     * METHOD: filter
     * DESCRIPTION:
     *   Intercepts every request passing through the gateway. Allows OPTIONS preflight
     *   and public paths through without authentication; validates the Bearer JWT for
     *   all other requests and returns 401 if the token is absent or invalid.
     * ================================================================ */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Handle OPTIONS preflight immediately
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        }

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("DEBUG: JwtAuthenticationFilter processing path: " + path);

        // Skip public endpoints
        if (path.contains("/api/auth")
                || path.contains("/api/operators")
                || path.contains("/swagger")
                || path.contains("/v3/api-docs")
                || path.contains("/fallback")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Enforce role-based access for admin endpoints
        if (path.contains("/admin/")) {
            try {
                String role = jwtUtil.getClaims(token).get("role", String.class);
                if (!"ADMIN".equals(role)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    /* ================================================================
     * METHOD: getOrder
     * DESCRIPTION:
     *   Returns the filter execution order. A value of -100 ensures this filter
     *   runs before most other gateway filters.
     * ================================================================ */
    @Override
    public int getOrder() {
        return -100;
    }
}
