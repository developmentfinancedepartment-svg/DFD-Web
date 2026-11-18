package com.logic.springjwt.DTO.SecurityDTO;
/**
 * @author M.M.Isuru Marasinghe (GitHub: isuru781)
 * @date 9/17/2025
 */
public class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
