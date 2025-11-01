package com.connexus.ticketing_service.service;

import com.connexus.ticketing_service.model.Ticket;
import com.connexus.ticketing_service.model.TicketStatus;
import com.connexus.ticketing_service.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketExpiryService {

    private final TicketRepository ticketRepository;

    public int expireOldTickets() {
        log.info("Running ticket expiry process...");

        List<Ticket> activeTickets = ticketRepository.findByStatus(TicketStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = 0;

        for (Ticket ticket : activeTickets) {
            LocalDateTime eventDateTime = LocalDateTime.of(
                    ticket.getEventDate() != null ? ticket.getEventDate() : LocalDate.now(),
                    ticket.getEventEndTime() != null ? ticket.getEventEndTime() : LocalTime.MIDNIGHT
            );

            if (eventDateTime.isBefore(now)) {
                ticket.setStatus(TicketStatus.EXPIRED);
                ticket.setLastVerifiedAt(now);
                ticketRepository.save(ticket);
                expiredCount++;
            }
        }

        log.info("Ticket expiry completed: {} tickets expired.", expiredCount);
        return expiredCount;
    }
}
