package com.logic.springjwt.repository;

import com.logic.springjwt.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByAddedEventAtDesc();
}
