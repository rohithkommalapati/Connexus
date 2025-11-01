package com.connexus.eventservice.exception;

public class FeignException extends RuntimeException {
    public FeignException(String message) {
        super(message);
    }
}
