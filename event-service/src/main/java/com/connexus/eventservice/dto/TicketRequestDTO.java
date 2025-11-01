package com.connexus.eventservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TicketRequestDTO {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    private Long attendeeId;
    private String attendeeName;

    public TicketRequestDTO(Long eventId, Long userId) {
        this.eventId = eventId;
        this.userId = userId;
    }
}

