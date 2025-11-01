package com.connexus.ticketing_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketRequestDTO {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    private Long attendeeId;
    private String attendeeName;
}

