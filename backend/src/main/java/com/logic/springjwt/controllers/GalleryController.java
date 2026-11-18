package com.logic.springjwt.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logic.springjwt.models.GalleryItem;
import com.logic.springjwt.repository.GalleryRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/gallery")
public class GalleryController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final GalleryRepository galleryRepository;

    public GalleryController(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @GetMapping
    public ResponseEntity<List<GalleryItem>> getAllGalleryItems(
            @RequestParam(value = "category", required = false) String category
    ) {
        if (category != null && !category.equalsIgnoreCase("All") && !category.trim().isEmpty()) {
            return ResponseEntity.ok(galleryRepository.findByCategoryOrderByIdDesc(category));
        }
        return ResponseEntity.ok(galleryRepository.findAllByOrderByIdDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GalleryItem> getGalleryItemById(@PathVariable Long id) {
        return galleryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createGalleryItem(
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam(value = "eventDate", required = false) String eventDate,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            List<MultipartFile> uploadList = collectFiles(images, files, image, file);

            GalleryItem item = new GalleryItem();
            item.setTitle(title);
            item.setCategory(category);
            item.setEventDate(eventDate != null ? eventDate : "");

            List<String> savedUrls = saveUploadedFiles(uploadList);

            if (!savedUrls.isEmpty()) {
                item.setImageUrl(savedUrls.get(0));
                item.setImagesList(savedUrls);
            } else {
                item.setImageUrl("");
                item.setImagesList(Collections.emptyList());
            }

            GalleryItem savedItem = galleryRepository.save(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to store gallery images: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating gallery item: " + e.getMessage());
        }
    }

    // Convenience overload for 5 parameters (backward compatibility with existing unit tests / callers)
    public ResponseEntity<?> createGalleryItem(
            String title,
            String category,
            String eventDate,
            MultipartFile image,
            MultipartFile file
    ) {
        return createGalleryItem(title, category, eventDate, null, null, image, file);
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> serveGalleryImage(@PathVariable String fileName) {
        try {
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "Gallery";
            Path filePath = Paths.get(uploadDir).resolve(fileName).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "image/jpeg";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateGalleryItemJson(@PathVariable Long id, @RequestBody GalleryItem updateData) {
        return galleryRepository.findById(id)
                .map(existingItem -> {
                    if (updateData.getTitle() != null) existingItem.setTitle(updateData.getTitle());
                    if (updateData.getCategory() != null) existingItem.setCategory(updateData.getCategory());
                    if (updateData.getEventDate() != null) existingItem.setEventDate(updateData.getEventDate());
                    if (updateData.getImagesRaw() != null) existingItem.setImagesRaw(updateData.getImagesRaw());
                    if (updateData.getImageUrl() != null && !updateData.getImageUrl().isEmpty()) {
                        existingItem.setImageUrl(updateData.getImageUrl());
                    } else if (!existingItem.getImages().isEmpty()) {
                        existingItem.setImageUrl(existingItem.getImages().get(0));
                    }
                    GalleryItem saved = galleryRepository.save(existingItem);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateGalleryItemMultipart(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "eventDate", required = false) String eventDate,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "existingImages", required = false) String existingImagesJson,
            @RequestParam(value = "coverImage", required = false) String coverImage
    ) {
        return galleryRepository.findById(id)
                .map(existingItem -> {
                    try {
                        if (title != null) existingItem.setTitle(title);
                        if (category != null) existingItem.setCategory(category);
                        if (eventDate != null) existingItem.setEventDate(eventDate);

                        List<String> previousImages = new ArrayList<>(existingItem.getImages());
                        List<String> retainedImages = new ArrayList<>();

                        if (existingImagesJson != null && !existingImagesJson.trim().isEmpty()) {
                            String trimmed = existingImagesJson.trim();
                            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                                try {
                                    List<String> parsed = OBJECT_MAPPER.readValue(trimmed, new TypeReference<List<String>>() {});
                                    if (parsed != null) retainedImages.addAll(parsed);
                                } catch (Exception ignored) {
                                }
                            } else {
                                String[] split = trimmed.split(",");
                                for (String s : split) {
                                    if (!s.trim().isEmpty()) retainedImages.add(s.trim());
                                }
                            }
                        } else if (existingImagesJson != null && existingImagesJson.trim().isEmpty()) {
                            // Explicit empty retained images
                            retainedImages.clear();
                        } else {
                            // If existingImages was not supplied, retain previous images unless single-file replacement
                            List<MultipartFile> newFilesCheck = collectFiles(images, files, image, file);
                            if (newFilesCheck.isEmpty()) {
                                retainedImages.addAll(previousImages);
                            }
                        }

                        // Remove deleted files from disk
                        for (String prev : previousImages) {
                            if (!retainedImages.contains(prev)) {
                                deleteFileFromStorage(prev);
                            }
                        }

                        // Save new uploaded files
                        List<MultipartFile> uploadList = collectFiles(images, files, image, file);
                        List<String> newUploadedUrls = saveUploadedFiles(uploadList);

                        List<String> finalImages = new ArrayList<>(retainedImages);
                        finalImages.addAll(newUploadedUrls);

                        if (coverImage != null && !coverImage.trim().isEmpty() && finalImages.contains(coverImage.trim())) {
                            existingItem.setImageUrl(coverImage.trim());
                        } else if (!finalImages.isEmpty()) {
                            existingItem.setImageUrl(finalImages.get(0));
                        } else {
                            existingItem.setImageUrl("");
                        }

                        existingItem.setImagesList(finalImages);

                        GalleryItem saved = galleryRepository.save(existingItem);
                        return ResponseEntity.ok(saved);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Failed to store gallery images: " + e.getMessage());
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error updating gallery item: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Convenience overload for 6 parameters (backward compatibility with existing unit tests / callers)
    public ResponseEntity<?> updateGalleryItemMultipart(
            Long id,
            String title,
            String category,
            String eventDate,
            MultipartFile image,
            MultipartFile file
    ) {
        return updateGalleryItemMultipart(id, title, category, eventDate, null, null, image, file, null, null);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<?> updateGalleryItem(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "eventDate", required = false) String eventDate
    ) {
        return updateGalleryItemMultipart(id, title, category, eventDate, null, null, null, null, null, null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGalleryItem(@PathVariable Long id) {
        return galleryRepository.findById(id)
                .map(item -> {
                    try {
                        List<String> allImages = item.getImages();
                        for (String imgUrl : allImages) {
                            deleteFileFromStorage(imgUrl);
                        }
                        if (item.getImageUrl() != null) {
                            deleteFileFromStorage(item.getImageUrl());
                        }
                    } catch (Exception ignored) {
                    }
                    galleryRepository.delete(item);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Private Helper Methods ---

    private List<MultipartFile> collectFiles(
            List<MultipartFile> images,
            List<MultipartFile> files,
            MultipartFile image,
            MultipartFile file
    ) {
        List<MultipartFile> result = new ArrayList<>();
        if (images != null) {
            for (MultipartFile f : images) {
                if (f != null && !f.isEmpty()) result.add(f);
            }
        }
        if (files != null) {
            for (MultipartFile f : files) {
                if (f != null && !f.isEmpty()) result.add(f);
            }
        }
        if (image != null && !image.isEmpty()) {
            result.add(image);
        }
        if (file != null && !file.isEmpty()) {
            result.add(file);
        }
        return result;
    }

    private List<String> saveUploadedFiles(List<MultipartFile> uploadFiles) throws IOException {
        List<String> savedUrls = new ArrayList<>();
        if (uploadFiles == null || uploadFiles.isEmpty()) {
            return savedUrls;
        }

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "Gallery";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        long timestamp = System.currentTimeMillis();
        int index = 0;
        for (MultipartFile uploadFile : uploadFiles) {
            String originalFilename = uploadFile.getOriginalFilename() != null ? uploadFile.getOriginalFilename() : "image.jpg";
            String cleanName = StringUtils.cleanPath(originalFilename);
            String fileName = timestamp + "_" + (index++) + "_" + UUID.randomUUID().toString().substring(0, 6) + "_" + cleanName;
            Path targetLocation = uploadPath.resolve(fileName);

            Files.copy(uploadFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            savedUrls.add("/uploads/Gallery/" + fileName);
        }

        return savedUrls;
    }

    private void deleteFileFromStorage(String webUrl) {
        if (webUrl == null || !webUrl.startsWith("/uploads/Gallery/")) return;
        try {
            String fileName = webUrl.substring("/uploads/Gallery/".length());
            Path filePath = Paths.get(System.getProperty("user.dir"), "uploads", "Gallery", fileName);
            Files.deleteIfExists(filePath);
        } catch (Exception ignored) {
        }
    }
}
