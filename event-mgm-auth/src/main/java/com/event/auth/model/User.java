package com.event.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Name must contain only letters and spaces")
    @Column(nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    @Size(max=50)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$"
            , message ="Password must contain at least one digit, one uppercase, one lowercase, and one special character")
    @Column(nullable = false)
    private String password;

    @Size(max = 255)
    private String socialProfileUrl;

    @Size(max = 500)
    private String bio;

    @Size(max = 100)
    private String skillsAndInterests;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }


}
