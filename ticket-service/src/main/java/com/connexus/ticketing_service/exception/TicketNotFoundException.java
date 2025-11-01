package com.connexus.ticketing_service.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String ticketUid) {
        super("Ticket not found: " + ticketUid);
    }
}