package com.logic.springjwt.controllers;

import com.logic.springjwt.models.Scheme;
import com.logic.springjwt.repository.SchemeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping({"/api/schemes", "/api/v1/services"})
public class SchemeController {

    private final SchemeRepository schemeRepository;

    public SchemeController(SchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    @GetMapping("/public")
    public ResponseEntity<List<Scheme>> getActiveSchemes() {
        return ResponseEntity.ok(schemeRepository.findPublicSchemes());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<Scheme> getPublicSchemeById(@PathVariable Long id) {
        return schemeRepository.findById(id)
                .filter(s -> !"DRAFT".equalsIgnoreCase(s.getStatus()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scheme> getSchemeById(@PathVariable Long id) {
        return schemeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Scheme>> getAllSchemes() {
        return ResponseEntity.ok(schemeRepository.findAll());
    }

    private String stripCitations(String text) {
        if (text == null) return null;
        return text.replaceAll("\\[cite:[^\\]]*\\]", "")
                   .replaceAll("【[^】]*】", "")
                   .trim();
    }

    private void sanitizeScheme(Scheme scheme) {
        if (scheme == null) return;
        scheme.setTitle(stripCitations(scheme.getTitle()));
        scheme.setDescription(stripCitations(scheme.getDescription()));
        scheme.setPurpose(stripCitations(scheme.getPurpose()));
        scheme.setEligibility(stripCitations(scheme.getEligibility()));
        scheme.setBeneficiaries(stripCitations(scheme.getBeneficiaries()));
        scheme.setRequirements(stripCitations(scheme.getRequirements()));
        scheme.setMaxAmount(stripCitations(scheme.getMaxAmount()));
        scheme.setMaxLoanSubtext(stripCitations(scheme.getMaxLoanSubtext()));
        scheme.setInterestRate(stripCitations(scheme.getInterestRate()));
        scheme.setInterestSubtext(stripCitations(scheme.getInterestSubtext()));
        scheme.setSubsidyBadge(stripCitations(scheme.getSubsidyBadge()));
        scheme.setRateFootnote(stripCitations(scheme.getRateFootnote()));
        scheme.setRepaymentPeriod(stripCitations(scheme.getRepaymentPeriod()));
        scheme.setRepaymentSubtext(stripCitations(scheme.getRepaymentSubtext()));
        scheme.setGracePeriod(stripCitations(scheme.getGracePeriod()));
        scheme.setGraceSubtext(stripCitations(scheme.getGraceSubtext()));
        scheme.setNatureOfLoan(stripCitations(scheme.getNatureOfLoan()));
        scheme.setSubsidyDetails(stripCitations(scheme.getSubsidyDetails()));
        scheme.setImplementationArrangements(stripCitations(scheme.getImplementationArrangements()));
        scheme.setOperationalArrangements(stripCitations(scheme.getOperationalArrangements()));
        scheme.setOperationalCostNote(stripCitations(scheme.getOperationalCostNote()));
        scheme.setOtherInitiatives(stripCitations(scheme.getOtherInitiatives()));
        scheme.setContactInfo(stripCitations(scheme.getContactInfo()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Scheme> createScheme(@RequestBody Scheme scheme) {
        scheme.setId(null);
        if (scheme.getSchemeType() == null || scheme.getSchemeType().trim().isEmpty()) {
            scheme.setSchemeType("LOAN");
        }
        if (scheme.getStatus() == null || scheme.getStatus().trim().isEmpty()) {
            scheme.setStatus(scheme.isActive() ? "ACTIVE" : "DRAFT");
        }
        if (scheme.getShowOtherBadge() == null) {
            scheme.setShowOtherBadge(false);
        }
        sanitizeScheme(scheme);
        Scheme savedScheme = schemeRepository.save(scheme);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedScheme);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Scheme> updateScheme(@PathVariable Long id, @RequestBody Scheme schemeDetails) {
        return schemeRepository.findById(id)
                .map(existingScheme -> {
                    existingScheme.setTitle(schemeDetails.getTitle());
                    existingScheme.setCategory(schemeDetails.getCategory());
                    existingScheme.setSchemeType(schemeDetails.getSchemeType() != null ? schemeDetails.getSchemeType() : "LOAN");
                    existingScheme.setMaxAmount(schemeDetails.getMaxAmount());
                    existingScheme.setMaxLoanSubtext(schemeDetails.getMaxLoanSubtext());
                    existingScheme.setInterestRate(schemeDetails.getInterestRate());
                    existingScheme.setInterestSubtext(schemeDetails.getInterestSubtext());
                    existingScheme.setSubsidyBadge(schemeDetails.getSubsidyBadge());
                    existingScheme.setRateFootnote(schemeDetails.getRateFootnote());
                    existingScheme.setNatureOfLoan(schemeDetails.getNatureOfLoan());
                    existingScheme.setSubsidyDetails(schemeDetails.getSubsidyDetails());
                    existingScheme.setDescription(schemeDetails.getDescription());
                    existingScheme.setIcon(schemeDetails.getIcon());
                    if (schemeDetails.getStatus() != null && !schemeDetails.getStatus().trim().isEmpty()) {
                        existingScheme.setStatus(schemeDetails.getStatus());
                    } else {
                        existingScheme.setActive(schemeDetails.isActive());
                    }
                    existingScheme.setRepaymentPeriod(schemeDetails.getRepaymentPeriod());
                    existingScheme.setRepaymentSubtext(schemeDetails.getRepaymentSubtext());
                    existingScheme.setGracePeriod(schemeDetails.getGracePeriod());
                    existingScheme.setGraceSubtext(schemeDetails.getGraceSubtext());
                    existingScheme.setPurpose(schemeDetails.getPurpose());
                    existingScheme.setEligibility(schemeDetails.getEligibility());
                    existingScheme.setParticipatingBanks(schemeDetails.getParticipatingBanks());
                    existingScheme.setBeneficiaries(schemeDetails.getBeneficiaries());
                    existingScheme.setRequirements(schemeDetails.getRequirements());
                    existingScheme.setLocationsRaw(schemeDetails.getLocationsRaw());
                    existingScheme.setServicesProvidedRaw(schemeDetails.getServicesProvidedRaw());
                    existingScheme.setCenterLocationsTableRaw(schemeDetails.getCenterLocationsTableRaw());
                    existingScheme.setLocationsTableRaw(schemeDetails.getLocationsTableRaw());
                    existingScheme.setCustomMetricsRaw(schemeDetails.getCustomMetricsRaw());
                    existingScheme.setOperationalArrangements(schemeDetails.getOperationalArrangements());
                    existingScheme.setOperationalCostNote(schemeDetails.getOperationalCostNote());
                    existingScheme.setImplementationArrangements(schemeDetails.getImplementationArrangements());
                    existingScheme.setOtherInitiatives(schemeDetails.getOtherInitiatives());
                    existingScheme.setContactInfo(schemeDetails.getContactInfo());
                    existingScheme.setOperationalGuidelineUrl(schemeDetails.getOperationalGuidelineUrl());
                    existingScheme.setOperationalGuidelineFileName(schemeDetails.getOperationalGuidelineFileName());
                    existingScheme.setShowOtherBadge(schemeDetails.getShowOtherBadge());
                    existingScheme.setCustomSectionsRaw(schemeDetails.getCustomSectionsRaw());

                    sanitizeScheme(existingScheme);
                    Scheme updatedScheme = schemeRepository.save(existingScheme);
                    return ResponseEntity.ok(updatedScheme);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/upload-guideline", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadGuideline(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File cannot be empty"));
        }

        try {
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "guideline.pdf";
            String cleanName = StringUtils.cleanPath(originalFilename);
            if (!cleanName.toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Only PDF files are permitted for operational guidelines"));
            }

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "Guidelines";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis() + "_" + cleanName;
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String webUrl = "/uploads/Guidelines/" + fileName;

            Map<String, Object> result = new HashMap<>();
            result.put("url", webUrl);
            result.put("operationalGuidelineUrl", webUrl);
            result.put("guidelineFileUrl", webUrl);
            result.put("fileName", cleanName);
            result.put("operationalGuidelineFileName", cleanName);
            result.put("guidelineFileName", cleanName);
            result.put("size", file.getSize());

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to upload file: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteScheme(@PathVariable Long id) {
        if (!schemeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        schemeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
