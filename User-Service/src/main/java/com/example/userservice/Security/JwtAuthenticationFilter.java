package com.example.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: JwtAuthenticationFilter
 * DESCRIPTION:
 *   Spring Security filter that intercepts each incoming HTTP request exactly once,
 *   extracts and validates the JWT from the Authorization header, and populates
 *   the SecurityContext with the authenticated user's credentials and role.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /* ================================================================
     * METHOD: doFilterInternal
     * DESCRIPTION:
     *   Reads the Bearer token from the Authorization header, validates it,
     *   and sets the authentication in the SecurityContext if the token is valid.
     *   Passes the request along the filter chain regardless of token presence.
     * ================================================================ */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            if (email != null && jwtUtil.validateToken(token, email) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // Note: Spring expects "ROLE_" prefix for .hasRole() checks
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, List.of(authority));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Log error; SecurityConfig will return 403/401
        }

        filterChain.doFilter(request, response);
    }
}
