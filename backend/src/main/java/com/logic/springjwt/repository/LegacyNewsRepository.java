package com.logic.springjwt.repository;

import com.logic.springjwt.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LegacyNewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByOrderByCreatedAtDesc();

    List<News> findTop5ByStatusOrderByCreatedAtDesc(int status);
}
