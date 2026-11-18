package com.logic.springjwt.DTO;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class UpdateStaffUnitDTO {

    @NotBlank
    private String staffUnit;
    private LocalDateTime latestUpdate;

    public String getStaffUnit() {
        return staffUnit;
    }

    public void setStaffUnit(String staffUnit) {
        this.staffUnit = staffUnit;
    }

    public LocalDateTime getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(LocalDateTime latestUpdate) {
        this.latestUpdate = latestUpdate;
    }
}
