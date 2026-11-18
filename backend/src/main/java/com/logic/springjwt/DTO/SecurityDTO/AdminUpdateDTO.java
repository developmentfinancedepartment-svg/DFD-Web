package com.logic.springjwt.DTO.SecurityDTO;

/**
 * DTO for Super Admin to update an existing admin's details.
 * Password is optional — if null/blank it will not be changed.
 */
public class AdminUpdateDTO {

    private String username;
    private String email;
    private String password; // optional — leave blank to keep existing
    private Boolean active;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
