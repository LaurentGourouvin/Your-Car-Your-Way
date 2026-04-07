package com.yourcaryourway.poc.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for generating and validating JWT tokens.
 * Used by Spring Security to authenticate incoming requests.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates the HMAC-SHA signing key from the secret defined in application.yml.
     *
     * @return the signing key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token containing the user's email, identifier and roles.
     *
     * @param email  the user's email address (used as the token subject)
     * @param userId the unique identifier of the user
     * @param roles  the list of user roles (e.g. ["CLIENT", "SUPPORT"])
     * @return the signed JWT token as a string
     */
    public String generateToken(String email, UUID userId, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the email address from a JWT token.
     *
     * @param token the JWT token
     * @return the email contained in the token subject
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the list of roles from a JWT token.
     *
     * @param token the JWT token
     * @return the list of user roles
     */
    public List<String> extractRoles(String token) {
        return extractClaims(token).get("roles", List.class);
    }

    /**
     * Checks whether a JWT token is valid and not expired.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses and extracts the claims contained in a JWT token.
     * Throws an exception if the token is invalid or expired.
     *
     * @param token the JWT token
     * @return the token claims
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}