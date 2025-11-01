package com.connexus.eventservice.exception;

public class AttendeeLimitReachedException extends RuntimeException {
    public AttendeeLimitReachedException(String message) { super(message); }
}