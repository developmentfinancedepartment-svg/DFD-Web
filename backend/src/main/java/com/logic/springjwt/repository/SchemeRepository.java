package com.logic.springjwt.repository;

import com.logic.springjwt.models.Scheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {
    List<Scheme> findByActiveTrue();

    @Query("SELECT s FROM Scheme s WHERE s.status IN ('ACTIVE', 'COMING_SOON') OR ((s.status IS NULL OR s.status = '') AND s.active = true)")
    List<Scheme> findPublicSchemes();
}
