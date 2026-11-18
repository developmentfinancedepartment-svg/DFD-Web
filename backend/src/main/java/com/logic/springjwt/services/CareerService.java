package com.logic.springjwt.services;

import com.logic.springjwt.models.CareerBanner;
import com.logic.springjwt.models.Intern;
import com.logic.springjwt.repository.CareerBannerRepository;
import com.logic.springjwt.repository.InternRepository;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CareerService {

    private final InternRepository internRepository;
    private final CareerBannerRepository bannerRepository;
    private final MultipartConfigElement multipartElement;

    public CareerService(InternRepository internRepository, CareerBannerRepository bannerRepository, MultipartConfigElement multipartElement) {
        this.internRepository = internRepository;
        this.bannerRepository = bannerRepository;
        this.multipartElement = multipartElement;
    }

    public List<Intern> getAllInterns() {
        return internRepository.findAll();
    }

    public Intern addIntern(Intern intern) {
        intern.setCreatedAt(LocalDateTime.now());
        return internRepository.save(intern);
    }

    public void deleteIntern(Long id) {
        internRepository.deleteById(id);
    }

    public CareerBanner saveBanner(MultipartFile file) throws IOException {
        String uploadDir = multipartElement.getLocation() + File.separator + "Careers";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + File.separator + fileName;

        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        CareerBanner banner = new CareerBanner();
        banner.setFileName(fileName);
        banner.setFileType(file.getContentType());
        banner.setFilePath(filePath);
        banner.setUploadedAt(LocalDateTime.now());

        return bannerRepository.save(banner);
    }

    public Optional<CareerBanner> getLatestBanner() {
        return bannerRepository.findFirstByOrderByUploadedAtDesc();
    }

    public void deleteBanner() {
        bannerRepository.deleteAll();
    }
}
