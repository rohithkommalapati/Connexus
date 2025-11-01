package com.connexus.eventservice.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super("Event with ID " + eventId + " not found");
    }
}
