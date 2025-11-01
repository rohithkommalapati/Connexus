package com.connexus.eventservice.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtInvalidTokenException extends AuthenticationException {
    public JwtInvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
    public JwtInvalidTokenException(String msg) {
        super(msg);
    }
}