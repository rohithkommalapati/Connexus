package com.connexus.ticketing_service.controller;

import com.connexus.ticketing_service.dto.TicketRequestDTO;
import com.connexus.ticketing_service.dto.TicketResponseDTO;
import com.connexus.ticketing_service.dto.TicketVerifyRequest;
import com.connexus.ticketing_service.dto.TicketVerifyResponse;
import com.connexus.ticketing_service.exception.InvalidSignatureException;
import com.connexus.ticketing_service.model.Ticket;
import com.connexus.ticketing_service.model.TicketStatus;
import com.connexus.ticketing_service.service.TicketService;
import com.connexus.ticketing_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/connexus/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO req) throws Exception {
        TicketResponseDTO dto = ticketService.createTicket(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** View ticket as Base64*/
    @GetMapping("/{ticketUid}/view")
    public ResponseEntity<Map<String, String>> getTicketView(
            @PathVariable String ticketUid,
            @RequestHeader("Authorization") String authHeader) throws Exception {

        Long userId = jwtUtil.extractUserId(authHeader);
        TicketResponseDTO ticket = ticketService.getTicketDetails(ticketUid);

        if (!ticket.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied"));
        }

        String base64Image = ticketService.generateTicketBase64(ticketUid);
        return ResponseEntity.ok(Map.of(
                "ticketUid", ticketUid,
                "imageBase64", base64Image
        ));
    }

    /** Download ticket as PNG file */
    @GetMapping("/{ticketUid}/download/png")
    public ResponseEntity<Resource> downloadTicketPng(
            @PathVariable String ticketUid,
            @RequestHeader("Authorization") String authHeader) throws Exception {

        Long userId = jwtUtil.extractUserId(authHeader);
        TicketResponseDTO ticket = ticketService.getTicketDetails(ticketUid);

        if (!ticket.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        Path pngPath = ticketService.generateTicketPng(ticketUid);

        Resource fileResource = new PathResource(pngPath);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + pngPath.getFileName())
                .body(fileResource);
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<List<TicketResponseDTO>> getAllTicketsForUser(@PathVariable Long userId) {
        List<TicketResponseDTO> tickets = ticketService.getAllTicketsForUser(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{ticketUid}")
    public ResponseEntity<TicketResponseDTO> getTicket(@PathVariable String ticketUid) {
        TicketResponseDTO dto = ticketService.getTicketDetails(ticketUid);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/verify")
    public ResponseEntity<TicketVerifyResponse> verify(@RequestBody TicketVerifyRequest req) throws Exception, InvalidSignatureException {
        TicketVerifyResponse res = ticketService.verify(req);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{ticketUid}/cancel")
    public ResponseEntity<TicketResponseDTO> cancelTicket(@PathVariable String ticketUid) {
        Ticket ticket = ticketService.updateTicketStatus(ticketUid, TicketStatus.CANCELLED);
        return ResponseEntity.ok(TicketResponseDTO.builderDto(ticket));
    }

    @PatchMapping("/{ticketUid}/status")
    public ResponseEntity<TicketResponseDTO> updateTicketStatus(
            @PathVariable String ticketUid,
            @RequestParam TicketStatus status) {
        Ticket ticket = ticketService.updateTicketStatus(ticketUid, TicketStatus.CANCELLED);
        return ResponseEntity.ok(TicketResponseDTO.builderDto(ticket));
    }

    @GetMapping("/{ticketUid}/qr")
    public ResponseEntity<?> getQrImage(@PathVariable String ticketUid) {
        TicketResponseDTO dto = ticketService.getTicketDetails(ticketUid);
        String path = dto.getQrCodeUrl();
        Path file = Path.of(path);
        if (!file.toFile().exists()) {
            return ResponseEntity.notFound().build();
        }
        PathResource resource = new PathResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}

