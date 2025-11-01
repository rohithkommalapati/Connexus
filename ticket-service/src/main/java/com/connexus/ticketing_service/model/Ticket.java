package com.connexus.ticketing_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 64)
    private String ticketUid;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String attendeeName;

    @Column(nullable = false, length = 255)
    private String eventTitle;

    @Column(length = 100)
    private String category;

    @Column(length = 255)
    private String location;

    @Column(length = 255)
    private String venue;

    private LocalDate eventDate;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.ACTIVE;

    @Column(length = 255)
    private String qrCodeUrl;

    @Column(length = 255)
    private String ticketPngUrl;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(length = 512)
    private String signature;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastVerifiedAt;
    private LocalDateTime checkInTime;
}

