package com.logic.springjwt.repository;

import com.logic.springjwt.models.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<GalleryItem, Long> {

    List<GalleryItem> findAllByOrderByIdDesc();

    List<GalleryItem> findByCategoryOrderByIdDesc(String category);
}
