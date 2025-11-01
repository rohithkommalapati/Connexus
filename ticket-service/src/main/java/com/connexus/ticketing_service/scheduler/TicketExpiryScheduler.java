package com.connexus.ticketing_service.scheduler;

import com.connexus.ticketing_service.service.TicketExpiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketExpiryScheduler {

    private final TicketExpiryService ticketExpiryService;

    @Scheduled(cron = "0 0 * * * *")
    public void scheduledExpireTickets() {
        log.info("Scheduled ticket expiry job triggered...");
        ticketExpiryService.expireOldTickets();
    }
}
