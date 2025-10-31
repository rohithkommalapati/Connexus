package com.event.auth.service;

import com.event.auth.exception.InvalidCredentialsException;
import com.event.auth.exception.UserAlreadyExistsException;
import com.event.auth.model.User;
import com.event.auth.repository.UserRepository;
import com.event.auth.util.JwtUtil;
import com.event.auth.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public User register(String firstName, String lastName, String email, String rawPassword) {
        log.info("Attempting to register user with email: {}", email);

        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Registration failed: Email {} already registered", email);
            throw new UserAlreadyExistsException("Email already registered");
        }
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return savedUser;
    }

    public String login(String email, String rawPassword) {
        log.info("Attempting login for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found for email {}", email);
                    return new InvalidCredentialsException("Invalid credentials");
                });

        if (!PasswordUtil.matches(rawPassword, user.getPassword())) {
            log.warn("Login failed: Invalid password for email {}", email);
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        log.info("JWT generated successfully for userId: {}", user.getId());

        return token;
    }
}
