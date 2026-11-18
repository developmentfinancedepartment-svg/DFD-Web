package com.logic.springjwt.repository;

import com.logic.springjwt.models.StaffUnits;
import com.logic.springjwt.projection.StaffUnitNameProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffUnitsRepository extends JpaRepository<StaffUnits, Long> {
    List<StaffUnitNameProjection> findAllBy();
}