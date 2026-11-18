package com.logic.springjwt.repository;

import com.logic.springjwt.models.NewsItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsItemRepository extends JpaRepository<NewsItem, Long> {
    List<NewsItem> findByActiveTrueOrderByCreatedAtDesc();
}
