package com.logic.springjwt.repository;

import com.logic.springjwt.models.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEvent, Long> {
    java.util.List<CalendarEvent> findByStatus(int status);
    java.util.List<CalendarEvent> findAllByOrderByEventDateDesc();
    java.util.List<CalendarEvent> findByStatusOrderByEventDateDesc(int status);
}
