package com.connexus.ticketing_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private String removeBearer(String token) {
        if (token == null) return null;
        token = token.trim();
        if (token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }

    public Claims extractAllClaims(String token) {
        String t = removeBearer(token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(t)
                .getBody();
    }

    public Long extractUserId(String token) {
        String sub = extractAllClaims(token).getSubject();
        return Long.parseLong(sub);
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public boolean hasValidSignature(String token) {
        extractAllClaims(token);
        return true;
    }
    public boolean isTokenValid(String token) {
        return hasValidSignature(token) && !isTokenExpired(token);
    }
}

