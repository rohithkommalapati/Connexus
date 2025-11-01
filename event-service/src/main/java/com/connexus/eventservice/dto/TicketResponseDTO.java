package com.connexus.eventservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponseDTO {
    private String ticketUid;
    private String status;
    private String qrCodeUrl;
    private LocalDateTime issuedAt;
    private String attendeeName;
    private Long userId;
    private Long eventId;
    private String eventTitle;
    private String eventDate;
    private String location;
    private String category;
    private String eventTime;
    private String eventEndTime;
}
