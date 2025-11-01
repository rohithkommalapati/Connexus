package com.connexus.ticketing_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketVerifyRequest {
    private String ticketId;
    private String payload;
    private boolean checkIn;
}
