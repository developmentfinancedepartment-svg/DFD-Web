package com.logic.springjwt.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "field_visits")
public class FieldVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String region; // 'Central', 'North Western', 'Eastern', 'Southern', etc.

    private String schemeType; // 'SME Loan', 'Microfinance', 'Green Finance', etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String keyTakeaways; // multiline text (one point per line)

    @Column(nullable = true)
    private String district;

    @Column(columnDefinition = "TEXT")
    private String coverImage;

    @Column(columnDefinition = "LONGTEXT")
    private String images;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public FieldVisit() {
    }

    public FieldVisit(Long id, String title, String region, String schemeType, String description, String keyTakeaways) {
        this.id = id;
        this.title = title;
        this.region = region;
        this.schemeType = schemeType;
        this.description = description;
        this.keyTakeaways = keyTakeaways;
        this.createdAt = LocalDateTime.now();
    }

    public FieldVisit(String title, String region, String schemeType, String description, String keyTakeaways) {
        this.title = title;
        this.region = region;
        this.schemeType = schemeType;
        this.description = description;
        this.keyTakeaways = keyTakeaways;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeyTakeaways() {
        return keyTakeaways;
    }

    public void setKeyTakeaways(String keyTakeaways) {
        this.keyTakeaways = keyTakeaways;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Aliases for compatibility
    public String getProvince() {
        return region;
    }

    public void setProvince(String province) {
        this.region = province;
    }

    public String getScheme() {
        return schemeType;
    }

    public void setScheme(String scheme) {
        this.schemeType = scheme;
    }

    public String getOverview() {
        return description;
    }

    public void setOverview(String overview) {
        this.description = overview;
    }

    public String getDistrict() {
        return district != null && !district.isEmpty() ? district : region;
    }

    public void setDistrict(String district) {
        this.district = district;
        if (this.region == null || this.region.isEmpty()) {
            this.region = district;
        }
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
