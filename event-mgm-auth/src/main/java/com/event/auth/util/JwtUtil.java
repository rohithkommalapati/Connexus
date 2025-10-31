package com.event.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long jwtExpiration;

    String secret;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long jwtExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    // Generate JWT
    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    private String removeBearer(String token) {
        if (token == null) return null;
        token = token.trim();
        if (token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }

    public Long extractUserId(String token) {
        String userIdStr = getClaims(token).getSubject();
        if (userIdStr == null || userIdStr.isEmpty()) {
            throw new IllegalArgumentException("User ID is missing or invalid in the token");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("User ID in the token is not a valid number", e);
        }
    }

    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public boolean isValidToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        String t = removeBearer(token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(t)
                .getBody();
    }

}
