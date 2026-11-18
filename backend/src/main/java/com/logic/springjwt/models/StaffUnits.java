package com.logic.springjwt.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "staff_unit")
public class StaffUnits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "staff_unit")
    private String staffUnit;

    @Column(name = "status")
    private int status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "latest_update")
    private LocalDateTime latestUpdate;

    @OneToMany(mappedBy = "staffUnits", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<StaffDetails> staffDetails = new ArrayList<>();

    public StaffUnits() {
    }

    public StaffUnits(String staffUnit, int status, LocalDateTime createdAt, LocalDateTime latestUpdate) {
        this.staffUnit = staffUnit;
        this.status = status;
        this.createdAt = createdAt;
        this.latestUpdate = latestUpdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaffUnit() {
        return staffUnit;
    }

    public void setStaffUnit(String staffUnit) {
        this.staffUnit = staffUnit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(LocalDateTime latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<StaffDetails> getStaffDetails() {
        return staffDetails;
    }

    public void setStaffDetails(List<StaffDetails> staffDetails) {
        this.staffDetails = staffDetails;
    }
}
