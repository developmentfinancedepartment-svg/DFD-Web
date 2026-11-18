package com.logic.springjwt.DTO.SecurityDTO;
/**
 * @author M.M.Isuru Marasinghe (GitHub: isuru781)
 * @date 9/17/2025
 */
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private boolean active;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
