package com.example.Api_Gateway.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: JwtUtil
 * DESCRIPTION:
 *   Utility component used by the API Gateway to validate incoming JWTs and extract
 *   their claims. Verifies token signatures using the shared HMAC-SHA secret key.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /* ================================================================
     * METHOD: getSigningKey
     * DESCRIPTION:
     *   Derives and returns the HMAC-SHA SecretKey from the configured JWT secret string.
     * ================================================================ */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /* ================================================================
     * METHOD: validateToken
     * DESCRIPTION:
     *   Validates the given JWT by verifying its signature and structure.
     *   Returns true if valid, false if any exception is thrown during parsing.
     * ================================================================ */
    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    /* ================================================================
     * METHOD: getClaims
     * DESCRIPTION:
     *   Parses the given JWT and returns all claims from its payload.
     *   Throws an exception if the token is invalid or expired.
     * ================================================================ */
    public Claims getClaims(String token){

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
