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

    private static class SavedFileInfo {
        final String fileName;
        final String webUrl;
        final String formattedSize;

        SavedFileInfo(String fileName, String webUrl, String formattedSize) {
            this.fileName = fileName;
            this.webUrl = webUrl;
            this.formattedSize = formattedSize;
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes >= 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else if (bytes >= 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return bytes + " B";
        }
    }

    private SavedFileInfo saveUploadedFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "Publications" +
                (subDir != null && !subDir.isEmpty() ? File.separator + subDir : "");
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String cleanName = StringUtils.cleanPath(originalFilename).replaceAll("\\s+", "_");
        String fileName = System.currentTimeMillis() + "_" + cleanName;
        Path targetLocation = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        String webUrl = "/uploads/Publications" + (subDir != null && !subDir.isEmpty() ? "/" + subDir : "") + "/" + fileName;
        return new SavedFileInfo(fileName, webUrl, formatFileSize(file.getSize()));
    }

    @GetMapping
    public ResponseEntity<List<Publication>> getActivePublications(
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "year", required = false) String year
    ) {
        String type = documentType != null ? documentType : category;
        List<Publication> list;
        if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("ALL")) {
            list = publicationRepository.findByActiveTrueAndDocumentTypeOrderByYearDescReleaseDateDescIdDesc(type.trim().toUpperCase());
        } else if (year != null && !year.trim().isEmpty() && !year.equalsIgnoreCase("ALL")) {
            list = publicationRepository.findByActiveTrueAndYearOrderByReleaseDateDescIdDesc(year.trim());
        } else {
            list = publicationRepository.findByActiveTrueOrderByYearDescReleaseDateDescIdDesc();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Publication>> getAllPublications() {
        return ResponseEntity.ok(publicationRepository.findAllByOrderByYearDescReleaseDateDescIdDesc());
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
            @RequestParam("releaseDate") String releaseDate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "fileEnglish", required = false) MultipartFile fileEnglish,
            @RequestParam(value = "fileSinhala", required = false) MultipartFile fileSinhala,
            @RequestParam(value = "fileTamil", required = false) MultipartFile fileTamil,
            @RequestParam(value = "file", required = false) MultipartFile legacyFile,
            @RequestParam(value = "coverImageUrl", required = false) String coverImageUrl,
            @RequestParam(value = "pdfEnglishUrl", required = false) String pdfEnglishUrl,
            @RequestParam(value = "pdfSinhalaUrl", required = false) String pdfSinhalaUrl,
            @RequestParam(value = "pdfTamilUrl", required = false) String pdfTamilUrl
    ) {
        try {
            Publication publication = new Publication();
            publication.setTitle(title);
            publication.setYear(year);
            publication.setDescription(description != null ? description : "");
            publication.setReleaseDate(releaseDate);
            publication.setActive(true);

            String finalDocType = documentType != null && !documentType.trim().isEmpty() ? documentType : category;
            if (finalDocType != null && !finalDocType.trim().isEmpty()) {
                publication.setDocumentType(finalDocType.trim().toUpperCase());
            }

            // Optional Cover Image
            if (coverImage != null && !coverImage.isEmpty()) {
                SavedFileInfo coverInfo = saveUploadedFile(coverImage, "Covers");
                if (coverInfo != null) {
                    publication.setCoverImageUrl(coverInfo.webUrl);
                }
            } else if (coverImageUrl != null && !coverImageUrl.trim().isEmpty()) {
                publication.setCoverImageUrl(coverImageUrl.trim());
            }

            // Map legacy file to English if fileEnglish is null
            MultipartFile enFile = (fileEnglish != null && !fileEnglish.isEmpty()) ? fileEnglish : legacyFile;

            // English PDF
            if (enFile != null && !enFile.isEmpty()) {
                SavedFileInfo enInfo = saveUploadedFile(enFile, null);
                if (enInfo != null) {
                    publication.setPdfEnglishUrl(enInfo.webUrl);
                    publication.setPdfEnglishName(enInfo.fileName);
                    publication.setPdfEnglishSize(enInfo.formattedSize);
                }
            } else if (pdfEnglishUrl != null && !pdfEnglishUrl.trim().isEmpty()) {
                publication.setPdfEnglishUrl(pdfEnglishUrl.trim());
                publication.setPdfEnglishSize("PDF");
            }

            // Sinhala PDF
            if (fileSinhala != null && !fileSinhala.isEmpty()) {
                SavedFileInfo siInfo = saveUploadedFile(fileSinhala, null);
                if (siInfo != null) {
                    publication.setPdfSinhalaUrl(siInfo.webUrl);
                    publication.setPdfSinhalaName(siInfo.fileName);
                    publication.setPdfSinhalaSize(siInfo.formattedSize);
                }
            } else if (pdfSinhalaUrl != null && !pdfSinhalaUrl.trim().isEmpty()) {
                publication.setPdfSinhalaUrl(pdfSinhalaUrl.trim());
                publication.setPdfSinhalaSize("PDF");
            }

            // Tamil PDF
            if (fileTamil != null && !fileTamil.isEmpty()) {
                SavedFileInfo taInfo = saveUploadedFile(fileTamil, null);
                if (taInfo != null) {
                    publication.setPdfTamilUrl(taInfo.webUrl);
                    publication.setPdfTamilName(taInfo.fileName);
                    publication.setPdfTamilSize(taInfo.formattedSize);
                }
            } else if (pdfTamilUrl != null && !pdfTamilUrl.trim().isEmpty()) {
                publication.setPdfTamilUrl(pdfTamilUrl.trim());
                publication.setPdfTamilSize("PDF");
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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePublication(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("year") String year,
            @RequestParam("releaseDate") String releaseDate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "fileEnglish", required = false) MultipartFile fileEnglish,
            @RequestParam(value = "fileSinhala", required = false) MultipartFile fileSinhala,
            @RequestParam(value = "fileTamil", required = false) MultipartFile fileTamil,
            @RequestParam(value = "file", required = false) MultipartFile legacyFile,
            @RequestParam(value = "removeCoverImage", defaultValue = "false") boolean removeCoverImage,
            @RequestParam(value = "removeEnglish", defaultValue = "false") boolean removeEnglish,
            @RequestParam(value = "removeSinhala", defaultValue = "false") boolean removeSinhala,
            @RequestParam(value = "removeTamil", defaultValue = "false") boolean removeTamil
    ) {
        return publicationRepository.findById(id)
                .map(publication -> {
                    try {
                        publication.setTitle(title);
                        publication.setYear(year);
                        publication.setDescription(description != null ? description : "");
                        publication.setReleaseDate(releaseDate);

                        String finalDocType = documentType != null && !documentType.trim().isEmpty() ? documentType : category;
                        if (finalDocType != null && !finalDocType.trim().isEmpty()) {
                            publication.setDocumentType(finalDocType.trim().toUpperCase());
                        }

                        // Cover Image removal / replacement
                        if (removeCoverImage) {
                            publication.setCoverImageUrl(null);
                        }
                        if (coverImage != null && !coverImage.isEmpty()) {
                            SavedFileInfo coverInfo = saveUploadedFile(coverImage, "Covers");
                            if (coverInfo != null) {
                                publication.setCoverImageUrl(coverInfo.webUrl);
                            }
                        }

                        // English File
                        if (removeEnglish) {
                            publication.setPdfEnglishUrl(null);
                            publication.setPdfEnglishName(null);
                            publication.setPdfEnglishSize(null);
                        }
                        MultipartFile enFile = (fileEnglish != null && !fileEnglish.isEmpty()) ? fileEnglish : legacyFile;
                        if (enFile != null && !enFile.isEmpty()) {
                            SavedFileInfo enInfo = saveUploadedFile(enFile, null);
                            if (enInfo != null) {
                                publication.setPdfEnglishUrl(enInfo.webUrl);
                                publication.setPdfEnglishName(enInfo.fileName);
                                publication.setPdfEnglishSize(enInfo.formattedSize);
                            }
                        }

                        // Sinhala File
                        if (removeSinhala) {
                            publication.setPdfSinhalaUrl(null);
                            publication.setPdfSinhalaName(null);
                            publication.setPdfSinhalaSize(null);
                        }
                        if (fileSinhala != null && !fileSinhala.isEmpty()) {
                            SavedFileInfo siInfo = saveUploadedFile(fileSinhala, null);
                            if (siInfo != null) {
                                publication.setPdfSinhalaUrl(siInfo.webUrl);
                                publication.setPdfSinhalaName(siInfo.fileName);
                                publication.setPdfSinhalaSize(siInfo.formattedSize);
                            }
                        }

                        // Tamil File
                        if (removeTamil) {
                            publication.setPdfTamilUrl(null);
                            publication.setPdfTamilName(null);
                            publication.setPdfTamilSize(null);
                        }
                        if (fileTamil != null && !fileTamil.isEmpty()) {
                            SavedFileInfo taInfo = saveUploadedFile(fileTamil, null);
                            if (taInfo != null) {
                                publication.setPdfTamilUrl(taInfo.webUrl);
                                publication.setPdfTamilName(taInfo.fileName);
                                publication.setPdfTamilSize(taInfo.formattedSize);
                            }
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
