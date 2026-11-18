package com.logic.springjwt.services;

import com.logic.springjwt.DTO.UpdateStatusDTO;
import com.logic.springjwt.models.HardwareSpecification;
import com.logic.springjwt.models.SpecifyDoc;
import com.logic.springjwt.repository.HardwareSpecificationRepository;
import com.logic.springjwt.repository.SpecifyDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HardwareSpecificationService {

    private final HardwareSpecificationRepository hardwareSpecificationRepository;
    private final SpecifyDocRepository specifyDocRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    public HardwareSpecificationService(
            HardwareSpecificationRepository hardwareSpecificationRepository,
            SpecifyDocRepository specifyDocRepository
    ) {
        this.hardwareSpecificationRepository = hardwareSpecificationRepository;
        this.specifyDocRepository = specifyDocRepository;

        // Failsafe migration: activate any old documents that defaulted to docStatus=0
        List<SpecifyDoc> allDocs = specifyDocRepository.findAll();
        boolean needsSave = false;
        for (SpecifyDoc doc : allDocs) {
            if (doc.getDocStatus() == 0) {
                doc.setDocStatus(1);
                needsSave = true;
            }
        }
        if (needsSave) {
            specifyDocRepository.saveAll(allDocs);
        }
    }

    /* ===================== SPECIFICATION ===================== */

    public HardwareSpecification saveSpecification(HardwareSpecification hardwareSpecification) {
        hardwareSpecification.setSpeCreatedAt(LocalDateTime.now());
        hardwareSpecification.setLatestUpdateAt(LocalDateTime.now());
        hardwareSpecification.setStatus(1); // Set default status to Active
        return hardwareSpecificationRepository.save(hardwareSpecification);
    }

    public List<HardwareSpecification> getAllHardwareSpecifications() {
        return hardwareSpecificationRepository.findAll();
    }

    public HardwareSpecification getSpecificationById(Long spcId) {
        return hardwareSpecificationRepository.findById(spcId)
                .orElseThrow(() -> new RuntimeException("Specification not found with ID: " + spcId));
    }

    /* ===================== DOCUMENT UPLOAD ===================== */

    public List<SpecifyDoc> saveSpecifyDocToSpecification(Long spcId, MultipartFile[] docs)
            throws IOException {

        HardwareSpecification specification = getSpecificationById(spcId);
        specification.setLatestUpdateAt(LocalDateTime.now());

        List<SpecifyDoc> savedDocs = new ArrayList<>();

        // BASE: <uploadDir>/Hardware Specification/<SpecificationType>
        Path specFolder = Paths.get(
                uploadDir,
                "Hardware Specification",
                specification.getSpecificationType()
        ).toAbsolutePath().normalize();

        Files.createDirectories(specFolder);

        for (MultipartFile doc : docs) {
            if (!isValidDoc(doc)) continue;

            String fileName = doc.getOriginalFilename();
            Path filePath = specFolder.resolve(fileName).normalize();

            // Overwrite if exists
            Files.copy(doc.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            SpecifyDoc specifyDoc = new SpecifyDoc();
            specifyDoc.setDocName(fileName);
            specifyDoc.setDocType(doc.getContentType());
            specifyDoc.setDocPath(filePath.toString());
            specifyDoc.setCreatedAt(LocalDateTime.now());
            specifyDoc.setLatestUpdateAt(LocalDateTime.now());
            specifyDoc.setDocStatus(1); // Set default doc status to Active
            specifyDoc.setHardwareSpecification(specification);

            savedDocs.add(specifyDocRepository.save(specifyDoc));
        }

        hardwareSpecificationRepository.save(specification); // Persist updated latestUpdateAt
        return savedDocs;
    }

    /* ===================== GET DOCUMENTS ===================== */

    public List<SpecifyDoc> getDocBySpecId(Long specId) {
        return specifyDocRepository.findByHardwareSpecificationId(specId);
    }

    public SpecifyDoc getDocByDocId(Long docId) {
        return specifyDocRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + docId));
    }

    /* ===================== DELETE ===================== */

    public void deleteSpecification(Long specId) {
        HardwareSpecification spec = getSpecificationById(specId);

        Path specFolder = Paths.get(
                uploadDir,
                "Hardware Specification",
                spec.getSpecificationType()
        );

        deleteFolder(specFolder);

        hardwareSpecificationRepository.delete(spec);
    }

    public void deleteDocFile(Long docId) {
        SpecifyDoc doc = getDocByDocId(docId);

        Path docPath = Paths.get(doc.getDocPath());
        try {
            Files.deleteIfExists(docPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete document file", e);
        }

        specifyDocRepository.deleteById(docId);
    }

    /* ===================== UPDATE STATUS ===================== */

    public HardwareSpecification updateSpecificationStatus(Long speId, UpdateStatusDTO dto) {
        HardwareSpecification spec = getSpecificationById(speId);
        spec.setStatus(dto.getStatus());
        spec.setLatestUpdateAt(LocalDateTime.now());
        return hardwareSpecificationRepository.save(spec);
    }

    public SpecifyDoc updateDocStatus(Long docId, UpdateStatusDTO dto) {
        SpecifyDoc doc = getDocByDocId(docId);
        doc.setDocStatus(dto.getStatus());
        doc.setLatestUpdateAt(LocalDateTime.now());
        return specifyDocRepository.save(doc);
    }

    /* ===================== HELPERS ===================== */

    private boolean isValidDoc(MultipartFile doc) {
        if (doc == null || doc.getOriginalFilename() == null) return false;
        String name = doc.getOriginalFilename().toLowerCase();
        return name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx");
    }

    private void deleteFolder(Path folderPath) {
        if (!Files.exists(folderPath)) return;

        try {
            Files.walk(folderPath)
                    .sorted((a, b) -> b.compareTo(a)) // delete children first
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to delete " + path, e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Error deleting folder " + folderPath, e);
        }
    }
}