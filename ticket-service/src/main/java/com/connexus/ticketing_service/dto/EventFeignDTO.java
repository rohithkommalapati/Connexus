package com.connexus.ticketing_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFeignDTO {
    private Long id;
    private String title;
    private String location;
    private String venue;
    private String category;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
