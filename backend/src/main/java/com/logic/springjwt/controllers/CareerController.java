package com.logic.springjwt.controllers;

import com.logic.springjwt.models.CareerBanner;
import com.logic.springjwt.models.Intern;
import com.logic.springjwt.services.CareerService;
import com.logic.springjwt.services.TransactionLogServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CareerController {

    @Value("${app.upload.dir:C:/uploads}")
    private String uploadDir;

    private final CareerService careerService;

    private final TransactionLogServices transactionLogServices;

    public CareerController(CareerService careerService, TransactionLogServices transactionLogServices) {
        this.careerService = careerService;
        this.transactionLogServices = transactionLogServices;
    }

    // --- Admin Endpoints ---

    @PostMapping("/admin/careers/banner")
    public ResponseEntity<CareerBanner> uploadBanner(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            CareerBanner banner = careerService.saveBanner(file);
            transactionLogServices.TransactionLog("careers", "BANNER_UPLOAD", authentication.getName(), banner.getFileName());
            return ResponseEntity.ok(banner);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/admin/careers/banner")
    public ResponseEntity<Void> deleteBanner(Authentication authentication) {
        careerService.deleteBanner();
        transactionLogServices.TransactionLog("careers", "BANNER_DELETE", authentication.getName(), "ALL");
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/admin/careers/interns")
    public List<Intern> getAllInternsAdmin() {
        return careerService.getAllInterns();
    }

    @PostMapping("/admin/careers/interns")
    public ResponseEntity<Intern> addIntern(@Valid @RequestBody Intern intern, Authentication authentication) {
        Intern savedIntern = careerService.addIntern(intern);
        transactionLogServices.TransactionLog("careers", "INTERN_ADD", authentication.getName(), savedIntern.getStudentId());
        return new ResponseEntity<>(savedIntern, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/careers/interns/{id}")
    public ResponseEntity<Void> deleteIntern(@PathVariable Long id, Authentication authentication) {
        careerService.deleteIntern(id);
        transactionLogServices.TransactionLog("careers", "INTERN_DELETE", authentication.getName(), id.toString());
        return ResponseEntity.noContent().build();
    }

    // --- Public Endpoints ---

    @GetMapping("/public/careers/banner")
    public ResponseEntity<CareerBanner> getLatestBanner() {
        Optional<CareerBanner> banner = careerService.getLatestBanner();
        return banner.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/public/careers/serve/banner/{fileName:.+}")
    public ResponseEntity<Resource> serveBanner(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, "Careers", fileName).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/png") // Adjust if needed
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/public/careers/interns")

    public List<Intern> getAllInternsPublic() {
        return careerService.getAllInterns();
    }
}
