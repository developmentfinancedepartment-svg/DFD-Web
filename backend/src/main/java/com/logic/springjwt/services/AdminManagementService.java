package com.logic.springjwt.services;

import com.logic.springjwt.DTO.SecurityDTO.AdminUpdateDTO;
import com.logic.springjwt.DTO.SecurityDTO.SignupRequest;
import com.logic.springjwt.models.SecurityModel.User;
import com.logic.springjwt.models.SecurityModel.UserRole;
import com.logic.springjwt.repository.Securityrepo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─── GET ALL ADMINS (ROLE_ADMIN only, never SUPERADMIN) ────────────────────
    public List<User> getAllAdmins() {
        return userRepository.findAllByRole(UserRole.ROLE_ADMIN);
    }

    // ─── GET SINGLE ADMIN BY ID ─────────────────────────────────────────────────
    public User getAdminById(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + userId));
        guardAgainstSuperAdmin(user);
        return user;
    }

    // ─── CREATE NEW ADMIN ───────────────────────────────────────────────────────
    public User createAdmin(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use: " + request.getEmail());
        }

        User newAdmin = new User();
        newAdmin.setUserId(UUID.randomUUID().toString());
        newAdmin.setUsername(request.getUsername());
        newAdmin.setEmail(request.getEmail());
        newAdmin.setPassword(passwordEncoder.encode(request.getPassword()));
        newAdmin.setRole(UserRole.ROLE_ADMIN);          // always ROLE_ADMIN
        newAdmin.setActive(request.isActive());

        return userRepository.save(newAdmin);
    }

    // ─── UPDATE ADMIN DETAILS ────────────────────────────────────────────────────
    public User updateAdmin(String userId, AdminUpdateDTO dto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + userId));

        guardAgainstSuperAdmin(user);

        // Username uniqueness check (skip if not changed)
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            if (!dto.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(dto.getUsername())) {
                throw new RuntimeException("Username is already taken: " + dto.getUsername());
            }
            user.setUsername(dto.getUsername());
        }

        // Email uniqueness check (skip if not changed)
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email is already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail());
        }

        // Password — only update if provided
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Active status
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }

        return userRepository.save(user);
    }

    // ─── TOGGLE ACTIVE / INACTIVE ────────────────────────────────────────────────
    public User toggleAdminStatus(String userId, boolean active) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + userId));
        guardAgainstSuperAdmin(user);
        user.setActive(active);
        return userRepository.save(user);
    }

    // ─── DELETE ADMIN ────────────────────────────────────────────────────────────
    public void deleteAdmin(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + userId));
        guardAgainstSuperAdmin(user);
        userRepository.delete(user);
    }

    // ─── SAFETY GUARD: NEVER TOUCH A SUPERADMIN ──────────────────────────────────
    private void guardAgainstSuperAdmin(User user) {
        if (user.getRole() == UserRole.ROLE_SUPERADMIN) {
            throw new RuntimeException("Operation not allowed: cannot modify a Super Admin account.");
        }
    }
}
