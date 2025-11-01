package com.connexus.eventservice.controller;

import com.connexus.eventservice.model.Event;
import com.connexus.eventservice.model.EventCategory;
import com.connexus.eventservice.service.EventService;
import com.connexus.eventservice.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final JwtUtil jwtUtil;

    public EventController(EventService eventService, JwtUtil jwtUtil) {
        this.eventService = eventService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody Event event, @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        Event created = eventService.createEvent(event, userId);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Event>> listEvents(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {

        List<Event> events = eventService.searchEvents(description, location, category, startDate, endDate);
        return ResponseEntity.ok(events);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id,
                                             @RequestBody Event event,
                                             @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        Event updated = eventService.updateEvent(id, event, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = jwtUtil.extractUserId(authHeader);
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
}