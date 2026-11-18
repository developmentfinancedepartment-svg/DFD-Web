package com.logic.springjwt.controllers;

import com.logic.springjwt.models.Publication;
import com.logic.springjwt.repository.PublicationRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/publications")
public class PublicationController {

    private final PublicationRepository publicationRepository;

    public PublicationController(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Publication>> getActivePublications() {
        return ResponseEntity.ok(publicationRepository.findByActiveTrueOrderByIdDesc());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Publication>> getAllPublications() {
        return ResponseEntity.ok(publicationRepository.findAllByOrderByIdDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublicationById(@PathVariable Long id) {
        return publicationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPublication(
            @RequestParam("title") String title,
            @RequestParam("year") String year,
            @RequestParam("description") String description,
            @RequestParam("releaseDate") String releaseDate,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            Publication publication = new Publication();
            publication.setTitle(title);
            publication.setYear(year);
            publication.setDescription(description);
            publication.setReleaseDate(releaseDate);
            publication.setActive(true);

            if (file != null && !file.isEmpty()) {
                long bytes = file.getSize();
                String formattedSize;
                if (bytes >= 1024 * 1024) {
                    formattedSize = String.format("%.1f MB", bytes / (1024.0 * 1024.0));
                } else {
                    formattedSize = String.format("%.1f KB", bytes / 1024.0);
                }
                publication.setFileSize(formattedSize);

                // 2. Define a clean, absolute upload directory using the project root
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "Publications";
                Path uploadPath = Paths.get(uploadDir);

                // 3. Ensure the directories exist before writing
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 4. Generate a unique filename using timestamp and sanitize it
                String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
                String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(originalFilename);
                Path targetLocation = uploadPath.resolve(fileName);

                // 5. Replace file.transferTo(...) with Files.copy
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                // 6. Save the accessible web URL path into the publication entity and persist it
                String webUrl = "/uploads/Publications/" + fileName;
                publication.setFileName(fileName);
                publication.setFileUrl(webUrl);
                publication.setUrl(webUrl);
            } else {
                publication.setFileSize("N/A");
                publication.setUrl("#");
            }

            Publication saved = publicationRepository.save(publication);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload publication file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating publication: " + e.getMessage());
        }
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> servePublicationFile(
            @PathVariable String fileName,
            @RequestParam(value = "download", defaultValue = "false") boolean download
    ) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "Publications";
            Path filePath = Paths.get(uploadDir).resolve(fileName).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String dispositionType = download ? "attachment" : "inline";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .header(HttpHeaders.CONTENT_DISPOSITION, dispositionType + "; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePublication(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("year") String year,
            @RequestParam("description") String description,
            @RequestParam("releaseDate") String releaseDate,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        return publicationRepository.findById(id)
                .map(publication -> {
                    try {
                        publication.setTitle(title);
                        publication.setYear(year);
                        publication.setDescription(description);
                        publication.setReleaseDate(releaseDate);

                        if (file != null && !file.isEmpty()) {
                            long bytes = file.getSize();
                            String formattedSize;
                            if (bytes >= 1024 * 1024) {
                                formattedSize = String.format("%.1f MB", bytes / (1024.0 * 1024.0));
                            } else {
                                formattedSize = String.format("%.1f KB", bytes / 1024.0);
                            }
                            publication.setFileSize(formattedSize);

                            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "Publications";
                            Path uploadPath = Paths.get(uploadDir);

                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }

                            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
                            String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(originalFilename);
                            Path targetLocation = uploadPath.resolve(fileName);

                            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                            String webUrl = "/uploads/Publications/" + fileName;
                            publication.setFileName(fileName);
                            publication.setFileUrl(webUrl);
                            publication.setUrl(webUrl);
                        }

                        Publication updated = publicationRepository.save(publication);
                        return ResponseEntity.ok(updated);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to upload publication file: " + e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error updating publication: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePublication(@PathVariable Long id) {
        return publicationRepository.findById(id)
                .map(pub -> {
                    publicationRepository.delete(pub);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Publication> toggleStatus(@PathVariable Long id) {
        return publicationRepository.findById(id)
                .map(pub -> {
                    pub.setActive(!pub.isActive());
                    Publication updated = publicationRepository.save(pub);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
