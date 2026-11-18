package com.logic.springjwt.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String eventName;
    private String moreContent;
    private int status;
    @Column(name = "added_event_at")
    private LocalDateTime addedEventAt;
    @Column(name = "latest_update_at")
    private LocalDateTime latestUpdateAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Media> media = new ArrayList<>();

    public Event() {
    }

    public Event(String eventName, String moreContent, int status, LocalDateTime addedEventAt, LocalDateTime latestUpdateAt) {
        this.eventName = eventName;
        this.moreContent = moreContent;
        this.status = status;
        this.addedEventAt = addedEventAt;
        this.latestUpdateAt = latestUpdateAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getMoreContent() {
        return moreContent;
    }

    public void setMoreContent(String moreContent) {
        this.moreContent = moreContent;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getAddedEventAt() {
        return addedEventAt;
    }

    public void setAddedEventAt(LocalDateTime addedEventAt) {
        this.addedEventAt = addedEventAt;
    }

    public LocalDateTime getLatestUpdateAt() {
        return latestUpdateAt;
    }

    public void setLatestUpdateAt(LocalDateTime latestUpdateAt) {
        this.latestUpdateAt = latestUpdateAt;
    }
}
