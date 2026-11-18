package com.logic.springjwt.repository;

import com.logic.springjwt.models.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findAllByOrderByDisplayOrderAscIdAsc();

    List<TeamMember> findByDivisionOrderByDisplayOrderAscIdAsc(String division);
}
