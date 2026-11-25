package com.logic.springjwt.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "publications")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String year;

    @Column(name = "document_type", length = 50)
    private String documentType = "ACTION_PLAN";

    @Column(columnDefinition = "TEXT")
    private String description;

    private String releaseDate;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    // English PDF Attachment
    @Column(name = "pdf_english_url")
    private String pdfEnglishUrl;

    @Column(name = "pdf_english_size")
    private String pdfEnglishSize;

    @Column(name = "pdf_english_name")
    private String pdfEnglishName;

    // Sinhala PDF Attachment
    @Column(name = "pdf_sinhala_url")
    private String pdfSinhalaUrl;

    @Column(name = "pdf_sinhala_size")
    private String pdfSinhalaSize;

    @Column(name = "pdf_sinhala_name")
    private String pdfSinhalaName;

    // Tamil PDF Attachment
    @Column(name = "pdf_tamil_url")
    private String pdfTamilUrl;

    @Column(name = "pdf_tamil_size")
    private String pdfTamilSize;

    @Column(name = "pdf_tamil_name")
    private String pdfTamilName;

    // Legacy fields maintained for backward compatibility
    private String fileSize;
    private String fileName;
    private String fileUrl;
    private String url;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void onSave() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        syncLegacyAndLanguageFields();
    }

    @PostLoad
    public void onLoad() {
        syncLegacyAndLanguageFields();
    }

    private void syncLegacyAndLanguageFields() {
        // Normalize document type / category
        if (this.documentType == null || this.documentType.trim().isEmpty()) {
            if (this.title != null) {
                String lower = this.title.toLowerCase();
                if (lower.contains("performance report") || lower.contains("annual report")) {
                    this.documentType = "ANNUAL_REPORT";
                } else if (lower.contains("circular") || lower.contains("policy")) {
                    this.documentType = "CIRCULAR";
                } else {
                    this.documentType = "ACTION_PLAN";
                }
            } else {
                this.documentType = "ACTION_PLAN";
            }
        }

        // Backward compatibility: If legacy fileUrl exists but pdfEnglishUrl is empty, populate English attachment
        if ((this.pdfEnglishUrl == null || this.pdfEnglishUrl.trim().isEmpty())
                && this.fileUrl != null && !this.fileUrl.trim().isEmpty() && !this.fileUrl.equals("#")) {
            this.pdfEnglishUrl = this.fileUrl;
            this.pdfEnglishSize = this.fileSize;
            this.pdfEnglishName = this.fileName;
        }

        // Backward compatibility: Populate legacy fileUrl from first available language
        if (this.fileUrl == null || this.fileUrl.trim().isEmpty() || this.fileUrl.equals("#")) {
            if (this.pdfEnglishUrl != null && !this.pdfEnglishUrl.isEmpty()) {
                this.fileUrl = this.pdfEnglishUrl;
                this.url = this.pdfEnglishUrl;
                this.fileName = this.pdfEnglishName;
                this.fileSize = this.pdfEnglishSize;
            } else if (this.pdfSinhalaUrl != null && !this.pdfSinhalaUrl.isEmpty()) {
                this.fileUrl = this.pdfSinhalaUrl;
                this.url = this.pdfSinhalaUrl;
                this.fileName = this.pdfSinhalaName;
                this.fileSize = this.pdfSinhalaSize;
            } else if (this.pdfTamilUrl != null && !this.pdfTamilUrl.isEmpty()) {
                this.fileUrl = this.pdfTamilUrl;
                this.url = this.pdfTamilUrl;
                this.fileName = this.pdfTamilName;
                this.fileSize = this.pdfTamilSize;
            }
        }
    }

    public Publication() {
    }

    public Publication(String title, String year, String description, String releaseDate, String fileSize, String fileName, String fileUrl, String url, boolean active) {
        this.title = title;
        this.year = year;
        this.description = description;
        this.releaseDate = releaseDate;
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.url = url;
        this.active = active;
        this.createdAt = LocalDateTime.now();
        syncLegacyAndLanguageFields();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDocumentType() {
        return documentType != null ? documentType : "ACTION_PLAN";
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    // Alias for category
    public String getCategory() {
        return getDocumentType();
    }

    public void setCategory(String category) {
        setDocumentType(category);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getPdfEnglishUrl() {
        return pdfEnglishUrl;
    }

    public void setPdfEnglishUrl(String pdfEnglishUrl) {
        this.pdfEnglishUrl = pdfEnglishUrl;
    }

    public String getPdfEnglishSize() {
        return pdfEnglishSize;
    }

    public void setPdfEnglishSize(String pdfEnglishSize) {
        this.pdfEnglishSize = pdfEnglishSize;
    }

    public String getPdfEnglishName() {
        return pdfEnglishName;
    }

    public void setPdfEnglishName(String pdfEnglishName) {
        this.pdfEnglishName = pdfEnglishName;
    }

    public String getPdfSinhalaUrl() {
        return pdfSinhalaUrl;
    }

    public void setPdfSinhalaUrl(String pdfSinhalaUrl) {
        this.pdfSinhalaUrl = pdfSinhalaUrl;
    }

    public String getPdfSinhalaSize() {
        return pdfSinhalaSize;
    }

    public void setPdfSinhalaSize(String pdfSinhalaSize) {
        this.pdfSinhalaSize = pdfSinhalaSize;
    }

    public String getPdfSinhalaName() {
        return pdfSinhalaName;
    }

    public void setPdfSinhalaName(String pdfSinhalaName) {
        this.pdfSinhalaName = pdfSinhalaName;
    }

    public String getPdfTamilUrl() {
        return pdfTamilUrl;
    }

    public void setPdfTamilUrl(String pdfTamilUrl) {
        this.pdfTamilUrl = pdfTamilUrl;
    }

    public String getPdfTamilSize() {
        return pdfTamilSize;
    }

    public void setPdfTamilSize(String pdfTamilSize) {
        this.pdfTamilSize = pdfTamilSize;
    }

    public String getPdfTamilName() {
        return pdfTamilName;
    }

    public void setPdfTamilName(String pdfTamilName) {
        this.pdfTamilName = pdfTamilName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
