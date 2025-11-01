package com.connexus.eventservice.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenExpiredException extends AuthenticationException {
    public JwtTokenExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public JwtTokenExpiredException(String msg) {
        super(msg);
    }
}