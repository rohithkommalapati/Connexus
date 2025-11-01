package com.connexus.eventservice.controller;

import com.connexus.eventservice.dto.RegistrationResponseDTO;
import com.connexus.eventservice.dto.UserDTO;
import com.connexus.eventservice.service.AttendeeService;
import com.connexus.eventservice.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class AttendeeController {

    private final AttendeeService attendeeService;
    private final JwtUtil jwtUtil;

    public AttendeeController(AttendeeService attendeeService, JwtUtil jwtUtil) {
        this.attendeeService = attendeeService;
        this.jwtUtil = jwtUtil;
    }

    // Register
    @PostMapping("/{eventId}/register")
    public ResponseEntity<RegistrationResponseDTO> register(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.extractUserId(authHeader);
        RegistrationResponseDTO dto = attendeeService.registerAttendee(eventId, userId);
        return ResponseEntity.ok(dto);
    }

    // Unregister
    @DeleteMapping("/{eventId}/unregister")
    public ResponseEntity<Void> unregister(
            @PathVariable Long eventId,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = jwtUtil.extractUserId(authHeader);
        attendeeService.unregisterAttendee(eventId, userId);
        return ResponseEntity.noContent().build();
    }

    // Get all attendees
    @GetMapping("/{eventId}/attendees")
    public ResponseEntity<List<UserDTO>> getAttendees(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendeeService.getAllAttendees(eventId));
    }

    // Check registration for given user
    @GetMapping("/{eventId}/is-registered")
    public ResponseEntity<Boolean> isRegistered(@PathVariable Long eventId, @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        return ResponseEntity.ok(attendeeService.isUserRegistered(eventId, userId));
    }
}

