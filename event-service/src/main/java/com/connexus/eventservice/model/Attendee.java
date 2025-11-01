package com.connexus.eventservice.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_attendees", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "user_id"})
})
@Getter
@Setter
@Builder
public class Attendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "event_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_attendee_event"))
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_attendee_user"))
    private UserReference user;

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(name = "ticket_id", length = 128)
    private String ticketId;

    private LocalDateTime deactivatedAt;

}
