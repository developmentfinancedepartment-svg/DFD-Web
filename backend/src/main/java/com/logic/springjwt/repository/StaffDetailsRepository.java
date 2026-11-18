package com.logic.springjwt.repository;


import com.logic.springjwt.models.StaffDetails;
import com.logic.springjwt.models.StaffUnits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffDetailsRepository extends JpaRepository<StaffDetails, Long> {
    @Query("SELECT s.staffUnits FROM StaffDetails s WHERE s.id = :detailsId")
    StaffUnits findUnitsByDetailsId(@Param("detailsId") Long detailsId);

    List<StaffDetails> findByNameContainingIgnoreCase(String name);

    List<StaffDetails> findAllByOrderByAddedAtDesc();
}
