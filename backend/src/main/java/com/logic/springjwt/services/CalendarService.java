package com.logic.springjwt.services;

import com.logic.springjwt.models.CalendarEvent;
import com.logic.springjwt.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public List<CalendarEvent> getAllEvents() {
        return calendarRepository.findAllByOrderByEventDateDesc();
    }

    public CalendarEvent saveEvent(CalendarEvent event) {
        return calendarRepository.save(event);
    }

    public void deleteEvent(Long id) {
        calendarRepository.deleteById(id);
    }

    public List<CalendarEvent> getActiveEvents() {
        return calendarRepository.findByStatusOrderByEventDateDesc(1);
    }

    public Optional<CalendarEvent> getEventById(Long id){
        return calendarRepository.findById(id);
    }
}
