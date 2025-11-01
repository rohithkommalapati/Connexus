package com.connexus.eventservice.service;

import com.connexus.eventservice.dto.RegistrationResponseDTO;
import com.connexus.eventservice.dto.TicketRequestDTO;
import com.connexus.eventservice.dto.TicketResponseDTO;
import com.connexus.eventservice.dto.UserDTO;
import com.connexus.eventservice.exception.*;
import com.connexus.eventservice.feign.TicketServiceClient;
import com.connexus.eventservice.feign.UserFeignClient;
import com.connexus.eventservice.model.Attendee;
import com.connexus.eventservice.model.Event;
import com.connexus.eventservice.model.UserReference;
import com.connexus.eventservice.repository.AttendeeRepository;
import com.connexus.eventservice.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AttendeeService {

    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final UserFeignClient userFeignClient;
    private final TicketServiceClient ticketServiceClient;

    @Value("${event.popularity.threshold:100}")
    private long popularityThreshold;

    public AttendeeService(EventRepository eventRepository,
                           AttendeeRepository attendeeRepository,
                           UserFeignClient userFeignClient,
                           TicketServiceClient ticketServiceClient) {
        this.eventRepository = eventRepository;
        this.attendeeRepository = attendeeRepository;
        this.userFeignClient = userFeignClient;
        this.ticketServiceClient = ticketServiceClient;
    }

    /**
     * Register a user for an event.
     * Generates a mock ticket (QR payload) and stores the ticketId reference.
     */
    @Transactional
    public RegistrationResponseDTO registerAttendee(Long eventId, Long userId) {
        log.info("Registering user={} for event={}", userId, eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        var isRegistered = attendeeRepository.findByEventIdAndUserId(eventId, userId);
        long activeCount = attendeeRepository.countActiveByEventId(eventId);

        if (event.getMaxAttendees() != null && activeCount >= event.getMaxAttendees()) {
            throw new AttendeeLimitReachedException("Attendee limit reached.");
        }
        if (isRegistered.isPresent() && isRegistered.get().isActive()) {
            throw new AlreadyRegisteredException("User already registered for this event.");
        }

        //user feign
        UserDTO user = userFeignClient.getUserById(userId);

        try {
            Attendee saved;
            if (isRegistered.isPresent()) {
                // reactivate previously-deactivated registration
                Attendee existing = isRegistered.get();
                existing.setActive(true);
                existing.setRegisteredAt(LocalDateTime.now());
                existing.setDeactivatedAt(null);

                TicketRequestDTO ticketReq = new TicketRequestDTO(eventId, userId);
                try {
                    TicketResponseDTO ticket = ticketServiceClient.createTicket(ticketReq);
                    existing.setTicketId(ticket.getTicketUid());
                } catch (Exception e) {
                    throw new FeignException("Error while fetching details from ticket service");
                }

                saved = attendeeRepository.save(existing);

            } else {
                // new registration
                UserReference userRef = new UserReference();
                userRef.setId(userId);

                Attendee attendee = Attendee.builder()
                        .event(event)
                        .user(userRef)
                        .registeredAt(LocalDateTime.now())
                        .isActive(true)
                        .build();

                saved = attendeeRepository.save(attendee);

                TicketRequestDTO ticketReq = new TicketRequestDTO(eventId, userId);
                try {
                    TicketResponseDTO ticket = ticketServiceClient.createTicket(ticketReq);
                } catch (Exception e) {
                    throw new FeignException("Error while creating ticket from ticket service");
                }
                saved = attendeeRepository.save(saved);
            }

            updatePopularityFlag(event);

            TicketResponseDTO ticketDto = ticketServiceClient.getTicket(saved.getTicketId());
            return RegistrationResponseDTO.builder()
                    .attendeeId(saved.getId())
                    .eventId(eventId)
                    .userId(userId)
                    .ticketId(saved.getTicketId())
                    .registeredAt(saved.getRegisteredAt())
                    .active(saved.isActive())
                    .build();

        } catch (RuntimeException ex) {
            throw new AlreadyRegisteredException("User already registered for this event.");
        }
    }

    /**
     * Unregister (cancel) a user's registration.
     * Deactivates the attendee record and tells ticket service to deactivate ticket.
     */
    @Transactional
    public void unregisterAttendee(Long eventId, Long userId) {
        log.info("Unregistering user={} from event={}", userId, eventId);

        Attendee attendee = attendeeRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new NotRegisteredException("User is not registered for this event."));

        if (!attendee.isActive()) {
            throw new NotRegisteredException("Registration already inactive.");
        }

        attendee.setActive(false);
        attendee.setDeactivatedAt(LocalDateTime.now());
        attendeeRepository.save(attendee);

        if (attendee.getTicketId() != null) {
            ticketServiceClient.cancelTicket(attendee.getTicketId());
        }

        // refresh event popularity
        Event event = attendee.getEvent();
        updatePopularityFlag(event);
    }

    /**
     * Return user info for active attendees of an event.
     */
    public List<UserDTO> getAllAttendees(Long eventId) {
        List<Attendee> attendees = attendeeRepository.findActiveByEventId(eventId);
        List<Long> userIds = attendees.stream()
                .map(a -> a.getUser().getId())
                .toList();
        if (userIds.isEmpty()) {
            return List.of();
        }

        return userFeignClient.getUsersByIds(userIds);
    }

    public boolean isUserRegistered(Long eventId, Long userId) {
        return attendeeRepository.existsActiveByEventIdAndUserId(eventId, userId);
    }

    /**
     * Recompute popularity for the event based on active attendee count and persist if changed.
     */
    private void updatePopularityFlag(Event event) {
        long attendeeCount = attendeeRepository.countActiveByEventId(event.getId());
        boolean shouldBePopular = attendeeCount >= popularityThreshold;
        Boolean current = event.getIsPopular();

        if (!current.equals(shouldBePopular)) {
            event.setIsPopular(shouldBePopular);
            eventRepository.save(event);
            log.info("Event {} popularity updated -> {} ({} attendees)", event.getId(), shouldBePopular, attendeeCount);
        }
    }
}
