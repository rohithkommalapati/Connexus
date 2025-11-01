package com.connexus.ticketing_service.repository;
import com.connexus.ticketing_service.model.Ticket;
import com.connexus.ticketing_service.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketUid(String ticketId);
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByStatus(TicketStatus status);

}
