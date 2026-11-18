package com.logic.springjwt.controllers;

import com.logic.springjwt.models.NewsItem;
import com.logic.springjwt.repository.NewsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsRepository newsRepository;

    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @GetMapping("/public")
    public ResponseEntity<List<NewsItem>> getActiveNews() {
        return ResponseEntity.ok(newsRepository.findByActiveTrueOrderByCreatedAtDesc());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NewsItem>> getAllNews() {
        return ResponseEntity.ok(newsRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsItem> createNews(@RequestBody NewsItem newsItem) {
        newsItem.setId(null);
        if (newsItem.getCreatedAt() == null) {
            newsItem.setCreatedAt(LocalDateTime.now());
        }
        NewsItem savedNews = newsRepository.save(newsItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewsItem> updateNews(@PathVariable Long id, @RequestBody NewsItem newsDetails) {
        return newsRepository.findById(id)
                .map(existingNews -> {
                    existingNews.setTitle(newsDetails.getTitle());
                    existingNews.setLinkUrl(newsDetails.getLinkUrl());
                    existingNews.setActive(newsDetails.isActive());
                    NewsItem updatedNews = newsRepository.save(existingNews);
                    return ResponseEntity.ok(updatedNews);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        if (!newsRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        newsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
