package com.logic.springjwt.repository;

import com.logic.springjwt.models.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    List<Publication> findByActiveTrueOrderByIdDesc();
    List<Publication> findAllByOrderByIdDesc();
    List<Publication> findByActiveTrueAndYear(String year);
}
