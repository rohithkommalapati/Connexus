package com.event.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequestDTO {

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain at least one digit, one uppercase, one lowercase, and one special character"
    )    @Size(min = 8, message = "Password must be at least 6 characters long")
    private String password;

}
