package com.connexus.ticketing_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs/expiry")
@RequiredArgsConstructor
public class ExpiryController {

    private final TicketExpiryService ticketExpiryService;

    @PostMapping("/expire-tickets")
    public ResponseEntity<String> triggerTicketExpiryManually() {
        int expiredCount = ticketExpiryService.expireOldTickets();
        return ResponseEntity.ok("Manual expiry job completed. " + expiredCount + " tickets expired.");
    }
}
