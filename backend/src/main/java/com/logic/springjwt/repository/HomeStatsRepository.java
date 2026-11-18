package com.logic.springjwt.repository;

import com.logic.springjwt.models.HomeStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomeStatsRepository extends JpaRepository<HomeStats, Long> {
    Optional<HomeStats> findFirstByOrderByIdAsc();
}
