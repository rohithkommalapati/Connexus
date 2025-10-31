package com.event.auth.controller;

import com.event.auth.dto.LoginRequestDTO;
import com.event.auth.dto.RegisterRequestDTO;
import com.event.auth.exception.UserAlreadyExistsException;
import com.event.auth.model.User;
import com.event.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/connexus/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDTO request) {
        User user = authService.register(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "email", user.getEmail()
        ));
    }

}
