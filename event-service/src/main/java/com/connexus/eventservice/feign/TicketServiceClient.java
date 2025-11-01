package com.connexus.eventservice.feign;

import com.connexus.eventservice.dto.TicketRequestDTO;
import com.connexus.eventservice.dto.TicketResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ticket-service", url = "${ticket.service.url}")
public interface TicketServiceClient {

    /** Create ticket */
    @PostMapping("/connexus/tickets/create")
    TicketResponseDTO createTicket(@RequestBody TicketRequestDTO req) throws Exception;

    /** Get ticket by UID */
    @GetMapping("/connexus/tickets/{ticketUid}")
    TicketResponseDTO getTicket(@PathVariable String ticketUid);

    /** Get all tickets for a user */
    @GetMapping("/connexus/tickets/{userId}/all")
    List<TicketResponseDTO> getAllTicketsForUser(@PathVariable Long userId);

    /** Cancel ticket */
    @PatchMapping("/connexus/tickets/{ticketUid}/cancel")
    TicketResponseDTO cancelTicket(@PathVariable String ticketUid);

    /** Update ticket status */
    @PatchMapping("/connexus/tickets/{ticketUid}/status")
    TicketResponseDTO updateTicketStatus(@PathVariable String ticketUid, @RequestParam TicketStatus status);

    /** Get QR image path (optional if needed) */
    @GetMapping("/connexus/tickets/{ticketUid}/qr")
    ResponseEntity<Resource> getQrImage(@PathVariable String ticketUid);

    /** Download ticket as PNG */
    @GetMapping("/connexus/tickets/{ticketUid}/download/png")
    ResponseEntity<Resource> downloadTicketPng(@PathVariable String ticketUid);

    /** Get ticket view as Base64 */
    @GetMapping("/connexus/tickets/{ticketUid}/view")
    ResponseEntity<Map<String, String>> getTicketView(@PathVariable String ticketUid);
}
