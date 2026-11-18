package com.logic.springjwt.controllers;

import com.logic.springjwt.models.TeamMember;
import com.logic.springjwt.repository.TeamMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/team-members")
public class TeamMemberController {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMemberController(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    @GetMapping
    public ResponseEntity<List<TeamMember>> getAllTeamMembers(
            @RequestParam(value = "division", required = false) String division
    ) {
        List<TeamMember> members;

        boolean hasDivision = division != null && !division.trim().isEmpty() && !division.equalsIgnoreCase("ALL");

        if (hasDivision) {
            members = teamMemberRepository.findByDivisionOrderByDisplayOrderAscIdAsc(division.trim());
        } else {
            members = teamMemberRepository.findAllByOrderByDisplayOrderAscIdAsc();
        }

        return ResponseEntity.ok(members);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamMember> getTeamMemberById(@PathVariable Long id) {
        return teamMemberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TeamMember> createTeamMember(@RequestBody TeamMember teamMember) {
        teamMember.setId(null);
        if (teamMember.getDisplayOrder() == null) {
            teamMember.setDisplayOrder(0);
        }
        TeamMember saved = teamMemberRepository.save(teamMember);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<TeamMember> updateTeamMember(@PathVariable Long id, @RequestBody TeamMember updated) {
        return teamMemberRepository.findById(id)
                .map(existing -> {
                    if (updated.getName() != null) existing.setName(updated.getName().trim());
                    if (updated.getDesignation() != null) existing.setDesignation(updated.getDesignation().trim());
                    if (updated.getDivision() != null) existing.setDivision(updated.getDivision().trim());
                    if (updated.getRoom() != null) existing.setRoom(updated.getRoom().trim());
                    if (updated.getDescription() != null) existing.setDescription(updated.getDescription().trim());
                    if (updated.getOfficePhone() != null) existing.setOfficePhone(updated.getOfficePhone().trim());
                    if (updated.getExtension() != null) existing.setExtension(updated.getExtension().trim());
                    if (updated.getMobilePhone() != null) existing.setMobilePhone(updated.getMobilePhone().trim());
                    if (updated.getFax() != null) existing.setFax(updated.getFax().trim());
                    if (updated.getPrimaryEmail() != null) existing.setPrimaryEmail(updated.getPrimaryEmail().trim());
                    if (updated.getSecondaryEmail() != null) existing.setSecondaryEmail(updated.getSecondaryEmail().trim());
                    if (updated.getDisplayOrder() != null) existing.setDisplayOrder(updated.getDisplayOrder());

                    TeamMember saved = teamMemberRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        if (!teamMemberRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        teamMemberRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
