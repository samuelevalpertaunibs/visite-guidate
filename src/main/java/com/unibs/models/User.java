package com.unibs.models;

import java.time.LocalDate;

public class User {
    private final String username;
    private String passwordHash;
    private final String role;
    private LocalDate lastLogin;
    private final byte[] salt;

    public User(String username, String passwordHash, byte[] salt, String role, LocalDate lastLogin) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.lastLogin = lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean checkPassword(String password) {
        return this.passwordHash.equals(password);
    }

    public byte[] getSalt() {
        return this.salt;
    }
}
