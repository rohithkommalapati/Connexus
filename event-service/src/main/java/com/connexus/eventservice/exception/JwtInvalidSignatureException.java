package com.connexus.eventservice.exception;

public class JwtInvalidSignatureException extends RuntimeException {
    public JwtInvalidSignatureException(String message, Exception ex) {
        super(message, ex);
    }
}
