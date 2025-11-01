package com.connexus.eventservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponseDTO {
    private Long attendeeId;
    private Long eventId;
    private Long userId;
    private String ticketId;
    private String qrCodeData;
    private LocalDateTime registeredAt;
    private boolean active;
}
