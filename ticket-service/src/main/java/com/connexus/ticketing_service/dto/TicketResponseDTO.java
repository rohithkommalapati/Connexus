package com.connexus.ticketing_service.dto;

import com.connexus.ticketing_service.model.Ticket;
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

    public static TicketResponseDTO builderDto(Ticket t) {
        return TicketResponseDTO.builder()
                .ticketUid(t.getTicketUid())
                .status(t.getStatus().name())
                .qrCodeUrl(t.getQrCodeUrl())
                .issuedAt(t.getIssuedAt())
                .attendeeName(t.getAttendeeName())
                .userId(t.getUserId())
                .eventId(t.getEventId())
                .eventTitle(t.getEventTitle())
                .location(t.getLocation())
                .category(t.getCategory())
                .eventDate(t.getEventDate() != null ? t.getEventDate().toString() : null)
                .eventTime(t.getEventStartTime() != null ? t.getEventStartTime().toString() : null)
                .eventTime(t.getEventEndTime() != null ? t.getEventEndTime().toString() : null)
                .build();
    }
}
