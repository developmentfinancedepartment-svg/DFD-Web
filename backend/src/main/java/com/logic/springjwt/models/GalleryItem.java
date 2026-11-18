package com.logic.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gallery_items")
public class GalleryItem {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category; // 'Workshops', 'Field Visits', 'Institutional'

    private String eventDate;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "images", columnDefinition = "LONGTEXT")
    private String images;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public GalleryItem() {
    }

    public GalleryItem(Long id, String title, String category, String eventDate, String imageUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public GalleryItem(Long id, String title, String category, String eventDate, String imageUrl, String images) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
        this.images = images;
        this.createdAt = LocalDateTime.now();
    }

    public GalleryItem(String title, String category, String eventDate, String imageUrl) {
        this.title = title;
        this.category = category;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("images")
    public List<String> getImages() {
        if (this.images != null && !this.images.trim().isEmpty()) {
            String trimmed = this.images.trim();
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                try {
                    List<String> parsed = OBJECT_MAPPER.readValue(trimmed, new TypeReference<List<String>>() {});
                    if (parsed != null && !parsed.isEmpty()) {
                        return parsed;
                    }
                } catch (Exception ignored) {
                }
            } else {
                List<String> single = new ArrayList<>();
                single.add(trimmed);
                return single;
            }
        }
        // Backward-compatible fallback: if images is empty but imageUrl is present
        if (this.imageUrl != null && !this.imageUrl.trim().isEmpty()) {
            List<String> fallback = new ArrayList<>();
            fallback.add(this.imageUrl.trim());
            return fallback;
        }
        return new ArrayList<>();
    }

    @JsonProperty("images")
    public void setImages(JsonNode node) {
        if (node == null || node.isNull()) {
            this.images = null;
        } else if (node.isArray()) {
            this.images = node.toString();
        } else if (node.isTextual()) {
            this.images = node.asText();
        } else {
            this.images = node.toString();
        }
    }

    @JsonIgnore
    public String getImagesRaw() {
        return this.images;
    }

    @JsonIgnore
    public void setImagesRaw(String images) {
        this.images = images;
    }

    public void setImagesList(List<String> list) {
        if (list == null || list.isEmpty()) {
            this.images = null;
        } else {
            try {
                this.images = OBJECT_MAPPER.writeValueAsString(list);
            } catch (Exception e) {
                this.images = null;
            }
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
