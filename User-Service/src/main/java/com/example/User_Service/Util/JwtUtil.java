package com.example.User_Service.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
  // from application properties
    @Value("${jwt.secret}")
    private String secret;
    @Getter
    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate Token — called in UserService.login()

    public String generateToken(String email, Long userId, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);   // needed for downstream services
        claims.put("role",   role);     // needed for SecurityConfig hasRole check

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Extract All Claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extract Email — used in JwtAuthenticationFilter

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extract Role — used in JwtAuthenticationFilter to set ROLE_USER/ADMIN
    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    // Extract UserId — useful for downstream services
    public Long extractUserId(String token) {
        return ((Number) extractAllClaims(token).get("userId")).longValue();
    }

    // Validate Token — used in JwtAuthenticationFilter
    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            Date   expiry     = extractAllClaims(token).getExpiration();
            return tokenEmail.equals(email) && expiry.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}