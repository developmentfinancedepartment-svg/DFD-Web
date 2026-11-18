package com.logic.springjwt.services;


import com.logic.springjwt.DTO.UpdateStatusDTO;
import com.logic.springjwt.models.ActionPdf;
import com.logic.springjwt.repository.ActionPdfRepository;
import jakarta.servlet.MultipartConfigElement;

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
public class ActionPdfService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final ActionPdfRepository actionPdfRepository;
    private final MultipartConfigElement multipartConfigElement;

    @Autowired
    public ActionPdfService(ActionPdfRepository actionPdfRepository, MultipartConfigElement multipartConfigElement) {
        this.actionPdfRepository = actionPdfRepository;
        this.multipartConfigElement = multipartConfigElement;
    }
    public ActionPdf saveAction(MultipartFile file) throws IOException {

        if (!isValidPdfFile(file)) {
            throw new IllegalArgumentException("Invalid file format. Only Pdf files are allowed!");
        }

        String actionPdfName = file.getOriginalFilename();

        Optional<ActionPdf> existingFile =
                actionPdfRepository.findByActionPdfName(actionPdfName);
        if (existingFile.isPresent()) {
            throw new IllegalArgumentException("This file already exists!");
        }

        // BASE UPLOAD DIR (from application.yml)
        File pdfDir = new File(uploadDir, "Action Plan Pdf");
        if (!pdfDir.exists()) pdfDir.mkdirs();

        File pdfFile = new File(pdfDir, actionPdfName);
        file.transferTo(pdfFile);

        ActionPdf actionPdf = new ActionPdf();
        actionPdf.setActionPdfName(actionPdfName);
        actionPdf.setActionPdfPath(pdfFile.getAbsolutePath());

        // IMAGE DIRECTORY
        File imgDir = new File(pdfDir, "Action Plan Image");
        if (!imgDir.exists()) imgDir.mkdirs();

        String actionImageName = actionPdfName.replace(".pdf", ".png");
        File imageFile = new File(imgDir, actionImageName);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 300, ImageType.RGB);
            ImageIO.write(image, "png", imageFile);
        }

        actionPdf.setActionImageName(actionImageName);
        actionPdf.setActionImagePath(imageFile.getAbsolutePath());
        actionPdf.setAddedPdfAt(LocalDateTime.now());
        actionPdf.setLatestStatusUpdateAt(LocalDateTime.now());

        return actionPdfRepository.save(actionPdf);
    }



    private boolean isValidPdfFile(MultipartFile file) {
        long maxSize = 15 * 1024 * 1024; // 15MB
        return file.getContentType().equalsIgnoreCase("application/pdf") && file.getSize() <= maxSize;
    }

    public List<ActionPdf> getAllActionFile() {
        return actionPdfRepository.findAllByOrderByAddedPdfAtDesc();
    }

    public ActionPdf findActionById(Long id) {
        return actionPdfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action Pdf not found with id: " + id));
    }

    public void deleteActionFile(Long id) {
        try {
            Optional<ActionPdf> optionalActionFile = actionPdfRepository.findById(id);
            if (optionalActionFile.isPresent()) {
                ActionPdf actionPdf = optionalActionFile.get();
                String pdfPath = actionPdf.getActionPdfPath();
                String imagePath = actionPdf.getActionImagePath();
                File pdfFile = new File(pdfPath);
                File imageFile = new File(imagePath);
                if (pdfFile.exists()) {
                    pdfFile.delete();
                }
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                actionPdfRepository.deleteById(id);

            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting action file with id: " + id, e);
        }

    }


    public ActionPdf updateActionStatus(Long id, UpdateStatusDTO updateStatusDTO) {
        ActionPdf tempAction = findActionById(id);
        updateStatusDTO.setUpdateAt(LocalDateTime.now());
        tempAction.setActionStatus(updateStatusDTO.getStatus());
        tempAction.setLatestStatusUpdateAt(updateStatusDTO.getUpdateAt());

        return actionPdfRepository.save(tempAction);
    }
}
