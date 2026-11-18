package com.logic.springjwt.controllers;

import com.logic.springjwt.DTO.SecurityDTO.AdminUpdateDTO;
import com.logic.springjwt.DTO.SecurityDTO.SignupRequest;
import com.logic.springjwt.DTO.SecurityDTO.AdminStatusDTO;
import com.logic.springjwt.models.SecurityModel.LogTransaction;
import com.logic.springjwt.models.SecurityModel.User;
import com.logic.springjwt.repository.Securityrepo.LogTransactionRepository;
import com.logic.springjwt.services.AdminManagementService;
import com.logic.springjwt.services.TransactionLogServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Super Admin Controller
 *
 * All endpoints require ROLE_SUPERADMIN.
 *
 * Admin Management:
 *   GET    /api/superadmin/admins              – list all admins
 *   GET    /api/superadmin/admins/{userId}     – get admin by id
 *   POST   /api/superadmin/admins/create       – create new admin
 *   PUT    /api/superadmin/admins/{userId}     – edit admin details
 *   PUT    /api/superadmin/admins/{userId}/status – toggle active/inactive
 *   DELETE /api/superadmin/admins/{userId}     – delete admin
 *
 * Log Viewing (super admin can see all logs):
 *   GET    /api/superadmin/logs                – all system logs
 *   GET    /api/superadmin/logs/{username}     – logs by specific admin username
 */
@RestController
@RequestMapping("/api/superadmin")
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperAdminController {

    private final AdminManagementService adminManagementService;
    private final TransactionLogServices transactionLogServices;
    private final LogTransactionRepository logTransactionRepository;

    public SuperAdminController(AdminManagementService adminManagementService,
                                TransactionLogServices transactionLogServices,
                                LogTransactionRepository logTransactionRepository) {
        this.adminManagementService = adminManagementService;
        this.transactionLogServices = transactionLogServices;
        this.logTransactionRepository = logTransactionRepository;
    }

    // ══════════════════════════════════════════════════
    //  ADMIN MANAGEMENT
    // ══════════════════════════════════════════════════

    /** List every ROLE_ADMIN account (super admins are never returned). */
    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        try {
            List<User> admins = adminManagementService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Get a single admin by their userId. Returns 403 if target is a super admin. */
    @GetMapping("/admins/{userId}")
    public ResponseEntity<?> getAdminById(@PathVariable String userId) {
        try {
            User admin = adminManagementService.getAdminById(userId);
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new admin account.
     * Body: { "username": "", "email": "", "password": "", "active": true }
     * The role is always forced to ROLE_ADMIN regardless of what the caller sends.
     */
    @PostMapping("/admins/create")
    public ResponseEntity<?> createAdmin(@RequestBody SignupRequest request,
                                         Authentication authentication) {
        try {
            User created = adminManagementService.createAdmin(request);
            transactionLogServices.TransactionLog(
                    "users",
                    "CREATE_ADMIN",
                    authentication.getName(),
                    created.getUserId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Edit an existing admin's details (username, email, password, active).
     * Forbidden if userId belongs to a super admin.
     */
    @PutMapping("/admins/{userId}")
    public ResponseEntity<?> updateAdmin(@PathVariable String userId,
                                          @RequestBody AdminUpdateDTO dto,
                                          Authentication authentication) {
        try {
            User updated = adminManagementService.updateAdmin(userId, dto);
            transactionLogServices.TransactionLog(
                    "users",
                    "UPDATE_ADMIN",
                    authentication.getName(),
                    userId
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Toggle active / inactive status for an admin.
     * Body: { "active": false }
     * Forbidden if userId belongs to a super admin.
     */
    @PutMapping("/admins/{userId}/status")
    public ResponseEntity<?> toggleAdminStatus(@PathVariable String userId,
                                                @RequestBody com.logic.springjwt.DTO.SecurityDTO.AdminStatusDTO statusDTO,
                                                Authentication authentication) {
        try {
            User updated = adminManagementService.toggleAdminStatus(userId, statusDTO.isActive());
            transactionLogServices.TransactionLog(
                    "users",
                    updated.isActive() ? "ACTIVATE_ADMIN" : "DEACTIVATE_ADMIN",
                    authentication.getName(),
                    userId
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete an admin account permanently.
     * Forbidden if userId belongs to a super admin.
     */
    @DeleteMapping("/admins/{userId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable String userId,
                                          Authentication authentication) {
        try {
            adminManagementService.deleteAdmin(userId);
            transactionLogServices.TransactionLog(
                    "users",
                    "DELETE_ADMIN",
                    authentication.getName(),
                    userId
            );
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ══════════════════════════════════════════════════
    //  LOG VIEWING  (super admin sees everything)
    // ══════════════════════════════════════════════════

    /** Return ALL transaction logs across all admins. */
    @GetMapping("/logs")
    public ResponseEntity<Iterable<LogTransaction>> getAllLogs() {
        try {
            Iterable<LogTransaction> logs = logTransactionRepository.findAll();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /** Return logs filtered by a specific admin username. */
    @GetMapping("/logs/{username}")
    public ResponseEntity<List<LogTransaction>> getLogsByAdmin(@PathVariable String username) {
        try {
            List<LogTransaction> logs = logTransactionRepository.findByUserName(username);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
