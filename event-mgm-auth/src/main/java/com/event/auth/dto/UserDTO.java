package com.event.auth.dto;

import com.event.auth.model.User;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String socialProfileUrl;
    private String bio;

    public static UserDTO mapDto(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getSocialProfileUrl(),
                user.getBio()
        );
    }
}