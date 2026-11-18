package com.logic.springjwt.controllers;


import com.logic.springjwt.DTO.UpdateNewsDTO;
import com.logic.springjwt.DTO.UpdateStaffDetailsDTO;
import com.logic.springjwt.DTO.UpdateStaffUnitDTO;
import com.logic.springjwt.DTO.UpdateStatusDTO;
import com.logic.springjwt.models.*;
import com.logic.springjwt.models.SecurityModel.LogTransaction;
import com.logic.springjwt.projection.StaffUnitNameProjection;
import com.logic.springjwt.repository.Securityrepo.LogTransactionRepository;
import com.logic.springjwt.services.*;
import jakarta.servlet.MultipartConfigElement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ActionPdfService actionPdfService;
    private final PerformancePdfService performancePdfService;
    private final NewsService newsService;
    private final EventService eventService;
    private final MultipartConfigElement multipartElement;
    private final HardwareSpecificationService hardwareSpecificationService;
    private final StaffService staffService;
    private final LogTransactionRepository logTransactionRepository;
    private final TransactionLogServices transactionLogServices;

    public AdminController(NewsService newsService, ActionPdfService actionPdfService, PerformancePdfService performancePdfService, EventService eventService, MultipartConfigElement multipartElement, HardwareSpecificationService hardwareSpecificationService, StaffService staffService, LogTransactionRepository logTransactionRepository, TransactionLogServices transactionLogServices) {
        this.actionPdfService = actionPdfService;
        this.performancePdfService = performancePdfService;
        this.newsService = newsService;
        this.eventService = eventService;
        this.multipartElement = multipartElement;
        this.hardwareSpecificationService = hardwareSpecificationService;
        this.staffService = staffService;
        this.logTransactionRepository = logTransactionRepository;
        this.transactionLogServices = transactionLogServices;
    }


    //    News admin endpoints
    @PostMapping("/addnews")
    public ResponseEntity<News> addNews(@Valid @RequestBody News data, Authentication authentication) {
        News addNewsItem = newsService.addNews(data);
        transactionLogServices.TransactionLog("news", "CREATE", authentication.getName(), addNewsItem.getId().toString());
        return new ResponseEntity<>(addNewsItem, HttpStatus.CREATED);
    }

    @GetMapping("/allnews")
    public List<News> getAllNews() {
        return newsService.getAllNewsOrderedByDate();
    }

    @GetMapping("/get/news/by/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        News getById = newsService.getNewsById(id);
        return new ResponseEntity<>(getById, HttpStatus.OK);
    }

    @PutMapping("/update/news/by/{id}")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @Valid @RequestBody UpdateNewsDTO updateNewsDTO, Authentication authentication) {
        News updateById = newsService.updateNews(id, updateNewsDTO);
        transactionLogServices.TransactionLog("news", "UPDATE", authentication.getName(), id.toString());
        return new ResponseEntity<>(updateById, HttpStatus.OK);
    }

    @PutMapping("/news/status/{id}")
    public ResponseEntity<News> updateNewsStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO, Authentication authentication) {
        try {
            News updatedNews = newsService.updateNewsStatus(id, updateStatusDTO);
            transactionLogServices.TransactionLog("news", "STATUS_UPDATE", authentication.getName(), id.toString());
            return ResponseEntity.ok(updatedNews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/deletenews/by/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id, Authentication authentication) {
        newsService.deleteNews(id);
        transactionLogServices.TransactionLog("news", "DELETE", authentication.getName(), id.toString());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    //    Action file admin endpoints
    @PostMapping("/upload/actionfile")
    public ResponseEntity<String> uploadActionFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            actionPdfService.saveAction(file);
            transactionLogServices.TransactionLog("action_pdf", "UPLOAD", authentication.getName(), file.getOriginalFilename());
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @PutMapping("/action/status/{id}")
    public ResponseEntity<ActionPdf> updateActionStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO, Authentication authentication) {
        try {
            ActionPdf updatedStatus = actionPdfService.updateActionStatus(id, updateStatusDTO);
            transactionLogServices.TransactionLog("action_pdf", "STATUS_UPDATE", authentication.getName(), id.toString());
            return ResponseEntity.ok(updatedStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/action/delete/{id}")
    public ResponseEntity<Void> deleteActionFile(@PathVariable Long id, Authentication authentication) {
        try {
            actionPdfService.deleteActionFile(id);
            transactionLogServices.TransactionLog("action_pdf", "DELETE", authentication.getName(), id.toString());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    Performance report admin endpoints

    @PostMapping("/upload/performancefile")
    public ResponseEntity<String> uploadPerformanceFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            performancePdfService.savePerformance(file);
            transactionLogServices.TransactionLog("performance_pdf", "UPLOAD", authentication.getName(), file.getOriginalFilename());
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @PutMapping("/performance/status/{id}")
    public ResponseEntity<PerformancePdf> updatePerformanceStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO, Authentication authentication) {
        try {
            PerformancePdf updatedStatus = performancePdfService.updatePerformanceStatus(id, updateStatusDTO);
            transactionLogServices.TransactionLog("performance_pdf", "STATUS_UPDATE", authentication.getName(), id.toString());
            return ResponseEntity.ok(updatedStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/performance/delete/{id}")
    public ResponseEntity<Void> deletePerformanceFile(@PathVariable Long id, Authentication authentication) {
        try {
            performancePdfService.deletePerformanceFile(id);
            transactionLogServices.TransactionLog("performance_pdf", "DELETE", authentication.getName(), id.toString());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    Events admin endpoints
    @PostMapping("/event/title/save")
    ResponseEntity<Event> createEvent(@RequestBody Event event, Authentication authentication) {
        try {
            Event createEvent = eventService.saveEvent(event);
            transactionLogServices.TransactionLog("events", "CREATE", authentication.getName(), createEvent.getId().toString());
            return ResponseEntity.ok(createEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/event/media/save/by/id/{eventId}")
    ResponseEntity<List<Media>> uploadMedia(@PathVariable Long eventId, @RequestParam("files") MultipartFile[] files) {
        try {
            Event event = eventService.getEventByID(eventId);
            event.setLatestUpdateAt(LocalDateTime.now());
            List<Media> contentList = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!eventService.isValidEventContent(file)) {
                    System.out.println("Invalid file format: " + file.getOriginalFilename());
                    continue;
                }
                String eventFolder = event.getEventName();
                String contentName = eventService.setContentNameAsUnique(file);
                String eventParentFolder = "uploadsEvents";
                String uploadDr = Paths.get(multipartElement.getLocation(), eventParentFolder).toString();
                String contentPath = Paths.get(uploadDr, eventFolder, contentName).toString();
                File directory = new File(uploadDr, eventFolder);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                try {
                    Files.copy(file.getInputStream(), Paths.get(contentPath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }

                Media media = new Media();
                media.setContentName(contentName);
                media.setContentType(file.getContentType());
                media.setContentPath(contentPath);
                media.setEvent(event);
                contentList.add(eventService.saveMedia(media));
            }
            return ResponseEntity.ok(contentList);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/event/status/{eventId}")
    public ResponseEntity<Event> updateEventStatus(@PathVariable Long eventId, @RequestBody UpdateStatusDTO updateStatusDTO, Authentication authentication) {
        try {
            Event updatedStatusEvent = eventService.updateEventStatus(eventId, updateStatusDTO);
            transactionLogServices.TransactionLog("events", "STATUS_UPDATE", authentication.getName(), eventId.toString());
            return ResponseEntity.ok(updatedStatusEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @DeleteMapping("/event/delete/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, Authentication authentication) {
        try {
            eventService.deleteEvent(eventId);
            transactionLogServices.TransactionLog("events", "DELETE", authentication.getName(), eventId.toString());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //    Hardware Specification endpoints
    @PostMapping("/hardware/specification")
    public ResponseEntity<HardwareSpecification> addSpecification(@RequestBody HardwareSpecification hardwareSpecification, Authentication authentication) {
        try {
            HardwareSpecification addSpecification = hardwareSpecificationService.saveSpecification(hardwareSpecification);
            transactionLogServices.TransactionLog("hardware_spec", "CREATE", authentication.getName(), addSpecification.getId().toString());
            return ResponseEntity.ok(addSpecification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/hardware/specification/doc/{speId}")
    public ResponseEntity<List<SpecifyDoc>> saveSpecifyDocToSpecification(@PathVariable Long speId, @RequestParam("docs") MultipartFile[] docs) {
        try {
            List<SpecifyDoc> saveDocs = hardwareSpecificationService.saveSpecifyDocToSpecification(speId, docs);
            return ResponseEntity.ok(saveDocs);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/hardware/specification/status/{speId}")
    public ResponseEntity<HardwareSpecification> updateSpecificationStatus(@PathVariable Long speId, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            HardwareSpecification updatedSpecification = hardwareSpecificationService.updateSpecificationStatus(speId, updateStatusDTO);
            return ResponseEntity.ok(updatedSpecification);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/hardware/specification/docs/status/{docId}")
    public ResponseEntity<SpecifyDoc> updateDocStatus(@PathVariable Long docId, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            SpecifyDoc updatedDoc = hardwareSpecificationService.updateDocStatus(docId, updateStatusDTO);
            return ResponseEntity.ok(updatedDoc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/hardware/specification/{specId}")
    public ResponseEntity<Void> deleteSpecification(@PathVariable Long specId, Authentication authentication) {
        try {
            hardwareSpecificationService.deleteSpecification(specId);
            transactionLogServices.TransactionLog("hardware_spec", "DELETE", authentication.getName(), specId.toString());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping("/hardware/specification/doc/{docId}")
    public ResponseEntity<Void> deleteDocFile(@PathVariable Long docId, Authentication authentication) {
        try {
            hardwareSpecificationService.deleteDocFile(docId);
            transactionLogServices.TransactionLog("hardware_spec_doc", "DELETE", authentication.getName(), docId.toString());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    staff manage endpoints
    @PostMapping("/staff/units")
    public ResponseEntity<StaffUnits> addStaffUnits(@RequestBody StaffUnits staffUnits) {
        try {
            StaffUnits addStaffUnit = staffService.saveStaffUnit(staffUnits);
            return ResponseEntity.ok(addStaffUnit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("staff/{unitId}/details")
    public ResponseEntity<StaffDetails> saveDetailsToUnit(@PathVariable Long unitId, @RequestBody StaffDetails staffDetails) {
        try {
            StaffDetails staffData = staffService.saveStaffToUnit(unitId, staffDetails);
            return ResponseEntity.ok(staffData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("staff/units/{unitId}")
    public ResponseEntity<StaffUnits> getUnitBYUnitId(@PathVariable Long unitId) {
        try {
            StaffUnits tempUnit = staffService.getUnitByUnitId(unitId);
            return ResponseEntity.ok(tempUnit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("staff/units/names")
    public ResponseEntity<List<StaffUnitNameProjection>> getAllUnitNames() {
        try {
            List<StaffUnitNameProjection> allNames = staffService.getAllUnitNames();
            return ResponseEntity.ok(allNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("staff/details")
    public ResponseEntity<List<StaffDetails>> getAllStaffDetails() {
        try {
            List<StaffDetails> allDetails = staffService.getAllStaffDetails();
            return ResponseEntity.ok(allDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("staff/details/{detailId}")
    public ResponseEntity<StaffDetails> getDetailByDetailId(@PathVariable Long detailId) {
        try {
            StaffDetails tempDetail = staffService.getDetailsByDetailsId(detailId);
            return ResponseEntity.ok(tempDetail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("staff/details/search")
    public ResponseEntity<List<StaffDetails>> searchByStaffName(@RequestParam("q") String query) {
        try {
            List<StaffDetails> result = staffService.searchByStaffName(query);

            if (result.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("staff/{detailId}/unit")
    public ResponseEntity<StaffUnits> getUnitByDetailId(@PathVariable Long detailId) {
        try {
            StaffUnits tempUnit = staffService.getUnitsByDetailsId(detailId);
            return ResponseEntity.ok(tempUnit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("staff/units/{unitId}/status")
    public ResponseEntity<StaffUnits> updateUnitStatus(@PathVariable Long unitId, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            StaffUnits updatedUnit = staffService.updateUnitStatus(unitId, updateStatusDTO);
            return ResponseEntity.ok(updatedUnit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("staff/details/{detailsId}/status")
    public ResponseEntity<StaffDetails> updateDetailsStatus(@PathVariable Long detailsId, @RequestBody UpdateStatusDTO updateStatusDTO) {
        try {
            StaffDetails updatedDetails = staffService.updateDetailsStatus(detailsId, updateStatusDTO);
            return ResponseEntity.ok(updatedDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("staff/units/{unitId}")
    public ResponseEntity<StaffUnits> updateStaffUnit(@PathVariable Long unitId, @Valid @RequestBody UpdateStaffUnitDTO unitDTO) {
        try {
            StaffUnits updatedUnit = staffService.updateUnitByUnitId(unitId, unitDTO);
            return ResponseEntity.ok(updatedUnit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("staff/details/{detailsId}")
    public ResponseEntity<StaffDetails> updateStaffDetails(@PathVariable Long detailsId, @RequestBody UpdateStaffDetailsDTO detailsDTO) {
        try {
            StaffDetails updatedDetails = staffService.updateDetailsByDetailsId(detailsId, detailsDTO);
            return ResponseEntity.ok(updatedDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("staff/units/{unitId}")
    public ResponseEntity<Void> deleteStaffUnit(@PathVariable Long unitId, Authentication authentication) {
        try {
            staffService.deleteStaffUnitByUnitId(unitId);
            transactionLogServices.TransactionLog("staff_unit", "DELETE", authentication.getName(), unitId.toString());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("staff/details/{detailsId}")
    public ResponseEntity<Void> deleteStaffDetails(@PathVariable Long detailsId, Authentication authentication) {
        try {
            staffService.deleteDetailsByDetailsId(detailsId);
            transactionLogServices.TransactionLog("staff", "DELETE", authentication.getName(), detailsId.toString());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ─── Admin Log endpoint ───────────────────────────────────────────────────
    /**
     * Returns the transaction log entries for the currently authenticated admin.
     * Each admin can only see their own logs (username is taken from the JWT).
     * GET /api/admin/logs
     */
    @GetMapping("/logs")
    public ResponseEntity<List<LogTransaction>> getMyLogs(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<LogTransaction> logs = logTransactionRepository.findByUserName(username);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
