package com.paymentService.securityConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Utility class for handling JWT (JSON Web Token) operations
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    /**
     * Extracts the username from the JWT token
     * @param token The JWT token string
     * @return The username stored in the token's subject claim
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the roles list from the JWT token
     * @param token The JWT token string
     * @return List of roles stored in the token's claims
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) extractClaims(token).get("roles", List.class);
    }

    /**
     * Validates if the provided JWT token is valid
     * @param token The JWT token string to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Helper method to extract all claims from the JWT token
     * @param token The JWT token string
     * @return Claims object containing all the claims from the token
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
