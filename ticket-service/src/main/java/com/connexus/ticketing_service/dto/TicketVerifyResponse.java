package com.connexus.ticketing_service.dto;

import com.connexus.ticketing_service.model.Ticket;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketVerifyResponse {
    private boolean valid;
    private String reason;
    private String ticketId;
    private String status;
    private String attendeeName;
    private String eventTitle;
    private String eventDate;
    private String eventStartTime;
    private String eventEndTime;

    public static TicketVerifyResponse buildVerificationResponse(Ticket t, boolean valid) {
        return TicketVerifyResponse.builder()
                .valid(valid)
                .ticketId(t.getTicketUid())
                .status(t.getStatus().name())
                .attendeeName(t.getAttendeeName())
                .eventTitle(t.getEventTitle())
                .eventDate(t.getEventDate() != null ? t.getEventDate().toString() : null)
                .eventStartTime(t.getEventStartTime() != null ? t.getEventStartTime().toString() : null)
                .eventEndTime(t.getEventEndTime() != null ? t.getEventEndTime().toString() : null)
                .build();
    }
}
