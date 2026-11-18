package com.logic.springjwt.controllers;

import com.logic.springjwt.models.CalendarEvent;
import com.logic.springjwt.services.CalendarService;
import com.logic.springjwt.DTO.UpdateStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Value("${app.upload.dir:}")
    private String uploadDir;

    // ================= GET EVENTS =================
    @GetMapping
    public List<CalendarEvent> getAllEvents(){
        return calendarService.getAllEvents();
    }

    // ================= GET ACTIVE EVENTS (PUBLIC) =================
    @GetMapping("/active")
    public List<CalendarEvent> getActiveEvents() {
        return calendarService.getActiveEvents();
    }

    // ================= ADD EVENT =================
    @PostMapping("/add")
    public CalendarEvent addEvent(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("eventDate") String eventDate,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam("location") String location,
            @RequestParam("status") int status,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        File dir = new File(uploadDir, "calendar_images");
        if(!dir.exists()){
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        File file = new File(dir, fileName);
        image.transferTo(file);

        CalendarEvent event = new CalendarEvent();
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDate(LocalDate.parse(eventDate));
        event.setStartTime(LocalTime.parse(startTime));
        event.setEndTime(LocalTime.parse(endTime));
        event.setLocation(location);
        event.setStatus(status);
        event.setImageUrl(fileName);

        return calendarService.saveEvent(event);
    }

    // ================= UPDATE EVENT =================
    @PostMapping("/update/{id}")
    public CalendarEvent updateEvent(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("eventDate") String eventDate,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            @RequestParam("location") String location,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {

        Optional<CalendarEvent> optionalEvent = calendarService.getEventById(id);

        if(optionalEvent.isEmpty()){
            throw new RuntimeException("Event not found with id " + id);
        }

        CalendarEvent event = optionalEvent.get();

        event.setTitle(title);
        event.setDescription(description);
        event.setEventDate(LocalDate.parse(eventDate));
        event.setStartTime(LocalTime.parse(startTime));
        event.setEndTime(LocalTime.parse(endTime));
        event.setLocation(location);
        event.setStatus(status);

        if(image != null && !image.isEmpty()){
            File dir = new File(uploadDir, "calendar_images");
            if(!dir.exists()){
                dir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            File file = new File(dir, fileName);
            image.transferTo(file);

            event.setImageUrl(fileName);
        }

        return calendarService.saveEvent(event);
    }

    // ================= DELETE EVENT =================
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id){
        calendarService.deleteEvent(id);
    }

    // ================= UPDATE STATUS =================
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateEventStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusDTO dto
    ) {
        Optional<CalendarEvent> optionalEvent = calendarService.getEventById(id);

        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Event not found");
        }

        CalendarEvent event = optionalEvent.get();
        event.setStatus(dto.getStatus());

        calendarService.saveEvent(event);

        return ResponseEntity.ok("Status updated successfully");
    }

    // ================= GET IMAGE =================
    @GetMapping("/image/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws MalformedURLException {

        Path imagePath = Paths.get(uploadDir, "calendar_images", imageName).toAbsolutePath().normalize();
        Resource resource = new UrlResource(imagePath.toUri());

        if(!resource.exists()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,"image/png")
                .body(resource);
    }
}