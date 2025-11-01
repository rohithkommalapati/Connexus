package com.connexus.eventservice.service;

import com.connexus.eventservice.exception.EventNotFoundException;
import com.connexus.eventservice.exception.UnauthorizedActionException;
import com.connexus.eventservice.model.Event;
import com.connexus.eventservice.model.EventCategory;
import com.connexus.eventservice.model.UserReference;
import com.connexus.eventservice.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event, Long userId) {
        log.info("Creating event for userId={}", userId);

        UserReference createdBy = new UserReference();
        createdBy.setId(userId);

        event.setCreatedBy(createdBy);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        log.info("Event created successfully with ID={}", saved.getId());
        return saved;
    }

    public Optional<Event> getEventById(Long id) {
        log.info("Fetching event with ID: {}", id);
        return eventRepository.findById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> searchEvents(String description, String location, EventCategory category,
                                    LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Searching events with filters - description: {}, location: {}, category: {}, startDate: {}, endDate: {}",
                description, location, category, startDate, endDate);

        List<Event> events;
        if (startDate != null && endDate != null) {
            events = eventRepository.findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(startDate, endDate);
        } else {
            events = eventRepository.findAll();
        }

        return events.stream()
                .filter(e -> (description == null ||
                        (e.getDescription() != null && e.getDescription().toLowerCase().contains(description.toLowerCase()))))
                .filter(e -> (location == null ||
                        (e.getLocation() != null && e.getLocation().toLowerCase().contains(location.toLowerCase()))))
                .filter(e -> (category == null || e.getCategory() == category))
                .toList();
    }


    public Event updateEvent(Long eventId, Event updatedEvent, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                {
                    log.warn("Event with ID={} not found for update", eventId);
                    return new EventNotFoundException(eventId);
                });
        if (!event.getCreatedBy().getId().equals(userId)) {
            log.warn("Unauthorized update attempt by user {} on event {}", userId, eventId);
            throw new UnauthorizedActionException("Unauthorized to update this event");
        }

        log.info("User {} updating event {}", userId, eventId);
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setCategory(updatedEvent.getCategory());
        event.setLocation(updatedEvent.getLocation());
        event.setStartTime(updatedEvent.getStartTime());
        event.setEndTime(updatedEvent.getEndTime());
        event.setMaxAttendees(updatedEvent.getMaxAttendees());
        event.setPrice(updatedEvent.getPrice());
        event.setImageUrl(updatedEvent.getImageUrl());
        event.setUpdatedAt(LocalDateTime.now());

        log.info("Event {} Updated Successfully", eventId);

        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId, Long userId) {
        log.info("User {} attempting to delete event ID={}", userId, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with ID={} not found for deletion", eventId);
                    return new EventNotFoundException(eventId);
                });
        if (!event.getCreatedBy().getId().equals(userId)) {
            log.warn("Unauthorized delete attempt by userId={} on eventId={}", userId, eventId);
            throw new UnauthorizedActionException("You are not authorized to delete this event");
        }

        event.setDeleted(true);
        event.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(event);

        log.info("Event ID={} deleted successfully by userId={}", eventId, userId);
    }

}
