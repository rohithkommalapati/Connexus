package com.event.auth.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    @Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "First name must contain only letters and spaces")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be 2-50 characters")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Last name must contain only letters and spaces")
    private String lastName;

    @Size(max = 500, message = "Maximum of 500 characters")
    private String bio;

    @Size(max = 100)
    private String skillsAndInterests;

    @Size(max = 255)
    private String socialProfileUrl;

    @Size(min = 8, max = 100, message = "Password must be 8-100 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain at least one digit, one uppercase, one lowercase, and one special character"
    )
    private String password;
}
