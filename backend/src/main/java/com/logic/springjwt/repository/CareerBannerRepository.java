package com.logic.springjwt.repository;

import com.logic.springjwt.models.CareerBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CareerBannerRepository extends JpaRepository<CareerBanner, Long> {
    Optional<CareerBanner> findFirstByOrderByUploadedAtDesc();
}
