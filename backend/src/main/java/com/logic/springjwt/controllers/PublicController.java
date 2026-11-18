package com.logic.springjwt.controllers;

import com.logic.springjwt.models.*;
import com.logic.springjwt.models.SecurityModel.UserDTO;
import com.logic.springjwt.security.services.UserDetailsImpl;
import com.logic.springjwt.services.*;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {


    @Value("${app.upload.dir:}")
    private String uploadDir;

    private final InquiriesService inquiriesService;
    private final NewsService newsService;

    private final ActionPdfService actionPdfService;
    private final PerformancePdfService performancePdfService;
    private final EventService eventService;
    private final HardwareSpecificationService hardwareSpecificationService;
    private final StaffService staffService;

    @Autowired
    private InformationSystemService service;

    private final MultipartConfigElement multipartElement;


    @Autowired
    public PublicController(InquiriesService inquiriesService, NewsService newsService, ActionPdfService actionPdfService, PerformancePdfService performancePdfService, EventService eventService, HardwareSpecificationService hardwareSpecificationService, StaffService staffService, MultipartConfigElement multipartElement) {
        this.inquiriesService = inquiriesService;
        this.newsService = newsService;
        this.actionPdfService = actionPdfService;
        this.performancePdfService = performancePdfService;
        this.eventService = eventService;
        this.hardwareSpecificationService = hardwareSpecificationService;
        this.staffService = staffService;
        this.multipartElement = multipartElement;
    }

    @PostMapping("/inquiries")
    public ResponseEntity<String> submitInq(@RequestBody InquiriesForm inquiriesForm) {
        try {
            inquiriesService.sendInquiries(inquiriesForm);
            inquiriesService.SendReplyEmail(inquiriesForm);

            return ResponseEntity.ok("Form submitted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error");

        }
    }

    //    News public endpoints
    @GetMapping("/latestnews")
    public List<News> getLast5News() {
        return newsService.getLast5ActiveNews();
    }

//   Action plane public endpoints

    @GetMapping("/getallactionfile")
    public List<ActionPdf> getAllActionFiles() {
        return actionPdfService.getAllActionFile();
    }

    @GetMapping("/actionpdf/{actionPdfName}")
    public ResponseEntity<Resource> serveActionPdfFile(@PathVariable String actionPdfName) {
        try {
            Path pdfPath = Paths.get(
                    uploadDir,
                    "Action Plan Pdf",
                    actionPdfName
            ).toAbsolutePath().normalize();

            Resource resource = new UrlResource(pdfPath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/actionimage/{actionImageName}")
    public ResponseEntity<Resource> serveActionImageFile(@PathVariable String actionImageName) {
        try {
            Path imagePath = Paths.get(
                    uploadDir,
                    "Action Plan Pdf",
                    "Action Plan Image",
                    actionImageName
            );

            System.out.println("Loading image from: " + imagePath.toAbsolutePath());

            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/png")
                        .body(resource);
            } else {
                throw new RuntimeException("Image file not found or unreadable");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not read the image file!", e);
        }
    }


    // Performance report public endpoints
    @GetMapping("/getallperformancefile")
    public List<PerformancePdf> getAllPerformanceFiles() {
        return performancePdfService.getAllPerformanceFile();
    }

@GetMapping("/performancepdf/{performancePdfName}")
public ResponseEntity<Resource> servePerformancePdfFile(
        @PathVariable String performancePdfName) {

    try {
        Path pdfPath = Paths.get(
                uploadDir,                    // C:/Uploads
                "Performance Report Pdf",     // folder
                performancePdfName            // file
        ).toAbsolutePath().normalize();

        System.out.println("Serving performance PDF from: " + pdfPath);

        Resource resource = new UrlResource(pdfPath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}


    @GetMapping("/performanceimage/{performanceImageName}")
public ResponseEntity<Resource> servePerformanceImageFile(@PathVariable String performanceImageName) {
    try {
        Path imagePath = Paths.get(
                uploadDir,                              // C:/Uploads
                "Performance Report Pdf",               // PDF folder
                "Performance Report Image",             // Image folder
                performanceImageName
        ).toAbsolutePath().normalize();

        System.out.println("Serving performance image from: " + imagePath);

        Resource resource = new UrlResource(imagePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(resource);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}




//    Events public endpoints

    @GetMapping("/event/get/all")
    public ResponseEntity<List<Event>> getAllEvent() {
        try {
            List<Event> events = eventService.getAllEvent();
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/event/get/content/by/{eventId}")
    public ResponseEntity<List<Media>> getContentByEventId(@PathVariable Long eventId) {
        try {
            List<Media> contentList = eventService.getContentByEventId(eventId);
            return ResponseEntity.ok(contentList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/event/serve/content/{contentName:.+}")
    public ResponseEntity<Resource> serveContentFile(@PathVariable String contentName) {
        try {
            Path contentPath = Paths.get(eventService.getContentPathByName(contentName)).toAbsolutePath().normalize();
            Resource resource = new UrlResource(contentPath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //    Hardware Specification endpoints
    @GetMapping("/hardware/specifications")
    public ResponseEntity<List<HardwareSpecification>> getAllHardwareSpecification() {
        try {
            List<HardwareSpecification> specifications = hardwareSpecificationService.getAllHardwareSpecifications();
            return ResponseEntity.ok(specifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/hardware/specification/docs/by/{specId}")
    public ResponseEntity<List<SpecifyDoc>> getDocBySpecId(@PathVariable Long specId) {
        try {
            List<SpecifyDoc> docList = hardwareSpecificationService.getDocBySpecId(specId);
            return ResponseEntity.ok(docList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/hardware/specification/doc/by/{docId}")
    public ResponseEntity<Resource> serveDocByDocId(@PathVariable Long docId) {
        try {
            SpecifyDoc tempDoc = hardwareSpecificationService.getDocByDocId(docId);
            Path docPath = Paths.get(tempDoc.getDocPath());
            Resource resource = new UrlResource(docPath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the DOC file!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read the DOC file!", e);
        }
    }

    //    Staff manage endpoints
    @GetMapping("staff/units")
    public ResponseEntity<List<StaffUnits>> getAllStaff() {
        try {
            List<StaffUnits> allData = staffService.getAllStaff();

            Comparator<StaffDetails> customComparator = Comparator
                    .comparingInt(details -> {
                        String designation = details.getDesignation();
                        if (designation.startsWith("Director General")) return 1;
                        if (designation.startsWith("Additional Director General")) return 2;
                        if (designation.startsWith("Director")) return 3;
                        if (designation.startsWith("Deputy Director")) return 4;
                        if (designation.startsWith("Assistant Director")) return 5;
                        if (designation.startsWith("ICT Officer")) return 6;
                        if (designation.startsWith("Development Officer")) return 7;
                        if (designation.startsWith("Statistical Officer")) return 8;
                        if (designation.startsWith("ICT Assistant")) return 9;
                        return 10;
                    });
            for (StaffUnits data : allData) {
                data.getStaffDetails().sort(customComparator);
            }

            return ResponseEntity.ok(allData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("staff/unit/{unitId}/details")
    public ResponseEntity<List<StaffDetails>> getDetailsByUnitId(@PathVariable Long unitId) {
        try {
            List<StaffDetails> detailsList = staffService.getDetailsByUnitId(unitId);
            return ResponseEntity.ok(detailsList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/get/me")
    public UserDTO getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new UserDTO(userDetails.getUserid(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getAuthorities());
    }


    @GetMapping("/hi")
    public String allAccess() {
        return "For public peoples!";
    }

    @GetMapping("/information-system/all")
    public List<InformationSystem> getAll() {
        return service.getAll();
    }

}


