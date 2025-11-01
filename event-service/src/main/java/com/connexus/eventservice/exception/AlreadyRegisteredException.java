package com.connexus.eventservice.exception;

public class AlreadyRegisteredException extends RuntimeException {
    public AlreadyRegisteredException(String message) { super(message); }
}