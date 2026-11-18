package com.logic.springjwt.services;

import com.logic.springjwt.DTO.UpdateStatusDTO;
import com.logic.springjwt.models.PerformancePdf;
import com.logic.springjwt.repository.PerformancePdfRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PerformancePdfService {

    @Value("${app.upload.dir}")
    private String uploadDir;


    private final PerformancePdfRepository performancePdfRepository;

    @Autowired
    public PerformancePdfService(PerformancePdfRepository performancePdfRepository) {
        this.performancePdfRepository = performancePdfRepository;
    }

    public PerformancePdf savePerformance(MultipartFile file) throws IOException {

        if (!isValidPdfFile(file)) {
            throw new IllegalArgumentException("Invalid file format. Only PDF files are allowed!");
        }

        String pdfName = file.getOriginalFilename();
        if (pdfName == null) {
            pdfName = "performance_" + System.currentTimeMillis() + ".pdf";
        }

        // Check duplicate
        Optional<PerformancePdf> existingFile =
                performancePdfRepository.findByPerformancePdfName(pdfName);
        if (existingFile.isPresent()) {
            throw new IllegalArgumentException("This PDF already exists!");
        }

        // BASE DIR from application.yml
        File pdfDir = new File(uploadDir, "Performance Report Pdf");
        if (!pdfDir.exists()) pdfDir.mkdirs();

        // Save PDF
        File pdfFile = new File(pdfDir, pdfName);
        file.transferTo(pdfFile);

        // Image folder
        File imgDir = new File(pdfDir, "Performance Report Image");
        if (!imgDir.exists()) imgDir.mkdirs();

        // Convert PDF → Image
        String imageName = pdfName.replaceAll("(?i)\\.pdf$", ".png");
        File imageFile = new File(imgDir, imageName);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image =
                    renderer.renderImageWithDPI(0, 300, ImageType.RGB);
            ImageIO.write(image, "png", imageFile);
        }

        // Save DB record
        PerformancePdf performancePdf = new PerformancePdf();
        performancePdf.setPerformancePdfName(pdfName);
        performancePdf.setPerformancePdfPath(pdfFile.getAbsolutePath());
        performancePdf.setPerformanceImageName(imageName);
        performancePdf.setPerformanceImagePath(imageFile.getAbsolutePath());
        performancePdf.setAddedPdfAt(LocalDateTime.now());
        performancePdf.setLatestUpdateAt(LocalDateTime.now());

        return performancePdfRepository.save(performancePdf);
    }


    private boolean isValidPdfFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        return (file.getContentType() != null && file.getContentType().equalsIgnoreCase("application/pdf"))
                || (name != null && name.toLowerCase().endsWith(".pdf"));
    }

    public List<PerformancePdf> getAllPerformanceFile() {
        return performancePdfRepository.findAllByOrderByAddedPdfAtDesc();
    }

    public PerformancePdf findPerformanceById(Long id) {
        return performancePdfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance PDF not found with id: " + id));
    }

    public void deletePerformanceFile(Long id) {
        PerformancePdf performancePdf = findPerformanceById(id);

        File pdfFile = new File(performancePdf.getPerformancePdfPath());
        File imageFile = new File(performancePdf.getPerformanceImagePath());

        if (pdfFile.exists()) pdfFile.delete();
        if (imageFile.exists()) imageFile.delete();

        performancePdfRepository.deleteById(id);
    }

    public PerformancePdf updatePerformanceStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        PerformancePdf performancePdf = findPerformanceById(id);
        performancePdf.setPerformanceStatus(updateStatusDTO.getStatus());
        performancePdf.setLatestUpdateAt(LocalDateTime.now());
        return performancePdfRepository.save(performancePdf);
    }
}
