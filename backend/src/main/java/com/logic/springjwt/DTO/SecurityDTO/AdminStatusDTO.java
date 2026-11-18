package com.logic.springjwt.DTO.SecurityDTO;

/**
 * Request body for toggling an admin's active/inactive status.
 * Send: { "active": true } or { "active": false }
 */
public class AdminStatusDTO {

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
