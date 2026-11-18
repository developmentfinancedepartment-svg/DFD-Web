package com.logic.springjwt.controllers;

import com.logic.springjwt.models.FieldVisit;
import com.logic.springjwt.repository.FieldVisitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/field-visits")
public class FieldVisitController {

    private final FieldVisitRepository fieldVisitRepository;

    public FieldVisitController(FieldVisitRepository fieldVisitRepository) {
        this.fieldVisitRepository = fieldVisitRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllFieldVisits(
            @RequestParam(value = "region", required = false) String region
    ) {
        List<FieldVisit> visits;
        if (region != null && !region.equalsIgnoreCase("All") && !region.trim().isEmpty()) {
            visits = fieldVisitRepository.findByRegionOrderByIdDesc(region);
        } else {
            visits = fieldVisitRepository.findAllByOrderByIdDesc();
        }

        long totalCount = fieldVisitRepository.count();
        long distinctProvincesCount = fieldVisitRepository.countDistinctRegions();

        Map<String, Object> response = new HashMap<>();
        response.put("visits", visits);
        response.put("totalCount", totalCount);
        response.put("distinctProvincesCount", distinctProvincesCount);
        response.put("beneficiariesEngaged", "8,500+");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldVisit> getFieldVisitById(@PathVariable Long id) {
        return fieldVisitRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FieldVisit> createFieldVisit(@RequestBody FieldVisit fieldVisit) {
        fieldVisit.setId(null);
        if (fieldVisit.getDistrict() != null && (fieldVisit.getRegion() == null || fieldVisit.getRegion().isEmpty())) {
            fieldVisit.setRegion(fieldVisit.getDistrict());
        }
        FieldVisit saved = fieldVisitRepository.save(fieldVisit);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<FieldVisit> updateFieldVisit(@PathVariable Long id, @RequestBody FieldVisit updated) {
        return fieldVisitRepository.findById(id)
                .map(existing -> {
                    if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
                    if (updated.getDistrict() != null) existing.setDistrict(updated.getDistrict());
                    if (updated.getRegion() != null) existing.setRegion(updated.getRegion());
                    if (updated.getSchemeType() != null) existing.setSchemeType(updated.getSchemeType());
                    if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
                    if (updated.getKeyTakeaways() != null) existing.setKeyTakeaways(updated.getKeyTakeaways());
                    if (updated.getCoverImage() != null) existing.setCoverImage(updated.getCoverImage());
                    if (updated.getImages() != null) existing.setImages(updated.getImages());
                    FieldVisit saved = fieldVisitRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFieldVisit(@PathVariable Long id) {
        if (!fieldVisitRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fieldVisitRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
