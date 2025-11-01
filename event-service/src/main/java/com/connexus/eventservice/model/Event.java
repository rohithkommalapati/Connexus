package com.connexus.eventservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Lob
    private String description;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private EventCategory category;

    @Size(max = 255)
    private String venue;

    @Size(max = 255)
    private String location;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @Min(value = 1)
    private Integer maxAttendees;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    private Double price;

    @Size(max = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by")
    private UserReference createdBy; // FK reference to User Service

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_popular", nullable = false)
    private Boolean isPopular = false;

    @Column(nullable = false)
    private boolean isDeleted = false;

}
