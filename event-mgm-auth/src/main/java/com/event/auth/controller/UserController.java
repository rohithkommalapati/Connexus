package com.event.auth.controller;

import com.event.auth.dto.UserDTO;
import com.event.auth.dto.UserUpdateDTO;
import com.event.auth.model.User;
import com.event.auth.service.UserService;
import com.event.auth.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/connexus/users")
@RequiredArgsConstructor
public class UserController{

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        log.info("Fetching current user with ID: {}", userId);

        User user = userService.getUserById(userId);
        log.info("Fetched user details: {}", user);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        log.info("Fetching user with ID: {}", userId);

        User user = userService.getUserById(userId);
        log.info("Fetching user details: {}", user);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserUpdateDTO request, @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        log.info("Updating user with ID: {} using data: {}", userId, request);

        User updatedUser = userService.updateUser(userId, request);
        log.info("Updated user details: {}", updatedUser);

        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/batch")
    public List<UserDTO> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

}
