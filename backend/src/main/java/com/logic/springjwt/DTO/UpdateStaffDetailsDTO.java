package com.logic.springjwt.DTO;


import java.time.LocalDateTime;

public class UpdateStaffDetailsDTO {
    private String designation;
    private String email;
    private String linkedIn;
    private String contactNumber;
    private LocalDateTime lastUpdateAt;
    private Long updatedUnitId;

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Long getUpdatedUnitId() {
        return updatedUnitId;
    }

    public void setUpdatedUnitId(Long updatedUnitId) {
        this.updatedUnitId = updatedUnitId;
    }

    public LocalDateTime getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(LocalDateTime lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }
}
