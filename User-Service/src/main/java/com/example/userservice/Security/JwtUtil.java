package com.example.userservice.security;

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

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: JwtUtil
 * DESCRIPTION:
 *   Utility component for generating, parsing, and validating JSON Web Tokens (JWTs).
 *   Embeds user email, ID, and role as claims and signs tokens with an HMAC-SHA key.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiration}")
    private long expiration;

    /* ================================================================
     * METHOD: getSigningKey
     * DESCRIPTION:
     *   Derives and returns the HMAC-SHA SecretKey from the configured JWT secret string.
     * ================================================================ */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /* ================================================================
     * METHOD: generateToken
     * DESCRIPTION:
     *   Builds and signs a JWT containing the user's email as the subject,
     *   along with userId and role as custom claims.
     * ================================================================ */
    public String generateToken(String email, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /* ================================================================
     * METHOD: extractEmail
     * DESCRIPTION:
     *   Extracts and returns the email (subject) claim from the given JWT.
     * ================================================================ */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /* ================================================================
     * METHOD: extractRole
     * DESCRIPTION:
     *   Extracts and returns the role claim from the given JWT.
     * ================================================================ */
    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("role");
    }

    /* ================================================================
     * METHOD: extractAllClaims
     * DESCRIPTION:
     *   Parses and verifies the JWT signature, returning all claims from the token payload.
     * ================================================================ */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /* ================================================================
     * METHOD: validateToken
     * DESCRIPTION:
     *   Validates the token by checking that the embedded email matches the provided
     *   email and that the token has not expired.
     * ================================================================ */
    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            return (tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /* ================================================================
     * METHOD: isTokenExpired
     * DESCRIPTION:
     *   Checks whether the token's expiration date is before the current time.
     * ================================================================ */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
