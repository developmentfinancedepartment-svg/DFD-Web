package com.logic.springjwt.repository;

import com.logic.springjwt.models.FieldVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldVisitRepository extends JpaRepository<FieldVisit, Long> {

    List<FieldVisit> findAllByOrderByIdDesc();

    List<FieldVisit> findByRegionOrderByIdDesc(String region);

    @Query("SELECT COUNT(DISTINCT f.region) FROM FieldVisit f")
    long countDistinctRegions();
}
