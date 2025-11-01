package com.connexus.eventservice.security;

import com.connexus.eventservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, java.io.IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("Authorization Header: [{}]", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7).trim();

            var claims = jwtUtil.extractAllClaims(token);

            Long userId = Long.parseLong(claims.getSubject());
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userId, null, null)
            );
            log.debug("Authenticated user with ID: {}", userId);
        }

        chain.doFilter(request, response);
    }
}
