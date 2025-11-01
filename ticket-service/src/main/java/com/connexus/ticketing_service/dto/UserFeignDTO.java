package com.connexus.ticketing_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFeignDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
