package com.connexus.eventservice.repository;

import com.connexus.eventservice.model.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Long> {

    List<Attendee> findByEventId(Long eventId);

    Optional<Attendee> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT a FROM Attendee a WHERE a.event.id = :eventId AND a.isActive = true")
    List<Attendee> findActiveByEventId(@Param("eventId") Long eventId);

    List<Attendee> findByUserId(Long userId);

    @Query("SELECT COUNT(a) FROM Attendee a WHERE a.event.id = :eventId AND a.isActive = true")
    long countActiveByEventId(@Param("eventId") Long eventId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Attendee a WHERE a.event.id = :eventId AND a.user.id = :userId AND a.isActive = true")
    boolean existsActiveByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") Long userId);
}
