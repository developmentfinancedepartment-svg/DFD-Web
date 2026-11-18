package com.logic.springjwt.models.SecurityModel;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserDTO {

    private String userId;
    private String username;
    private String email;
    private String password;
    private UserRole role;
    private boolean active;

    public UserDTO(String id, String username, String email, Collection<? extends GrantedAuthority> authorities) {
    }

    public UserDTO(String userId, String username, String email, String password, UserRole role, boolean active) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
