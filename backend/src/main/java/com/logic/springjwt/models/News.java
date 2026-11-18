package com.logic.springjwt.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 200)
    private String title;
    @NotBlank
    @Size(max = 1000)
    private String content;
    private int status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "latest_update_at")
    private LocalDateTime latestUpdateAt;

    @Size(max = 500)
    private String newsLink;

    public News() {

    }

    public News(String title, String content, String newsLink, int status, LocalDateTime createdAt, LocalDateTime latestUpdateAt) {
        this.title = title;
        this.content = content;
        this.newsLink = newsLink;
        this.status = status;
        this.createdAt = createdAt;
        this.latestUpdateAt = latestUpdateAt;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewsLink() {
        return newsLink;
    }

    public void setNewsLink(String newsLink) {
        this.newsLink = newsLink;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLatestUpdateAt() {
        return latestUpdateAt;
    }

    public void setLatestUpdateAt(LocalDateTime latestUpdateAt) {
        this.latestUpdateAt = latestUpdateAt;
    }
}
