package com.unibs.models;

import java.time.LocalDate;

public class User {
    private final String username;
    private String password;
    private final String role;
    private LocalDate lastLogin;

    public User(String username, String password, String role, LocalDate lastLogin) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.lastLogin = lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}
