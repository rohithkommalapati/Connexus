package com.event.auth.security;

import com.event.auth.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/connexus/auth/") || path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        Long userId = null;
        String email = null;
        log.debug("Incoming request [{} {}]", request.getMethod(), request.getRequestURI());

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.debug("Authorization header found, extracting JWT token");
            try {
                userId = jwtUtil.extractUserId(token);
                email = jwtUtil.extractEmail(token);
                log.info("Extracted userId: {}, email: {} from JWT", userId, email);
            } catch (JwtException e) {
                log.error("JWT validation failed: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }
        } else {
            log.debug("No valid Authorization header found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header missing");
            return;
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.isValidToken(token)) {
                log.info("Valid JWT for userId: {}. Setting authentication context.", userId);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

                authToken.setDetails(email);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authentication set for userId: {}, email: {}", userId, email);

            } else {
                log.warn("JWT token validation failed for userId: {}", userId);
            }
        }
        else {
            if (userId == null) {
                log.debug("No userId extracted from token");
            } else {
                log.debug("Authentication already present in context, skipping");
            }
        }
        filterChain.doFilter(request, response);
    }

}

