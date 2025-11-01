package com.connexus.eventservice.repository;

import com.connexus.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedById(Long userId);

    List<Event> findByIsPopularTrue();

    List<Event> findByCategory(String category);

    //Fetch events within a date range
    List<Event> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(LocalDateTime startDate, LocalDateTime endDate);

    List<Event> findByTitle(String keyword);

}
