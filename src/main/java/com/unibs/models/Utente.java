package com.unibs.models;

import java.time.LocalDate;

public class Utente {
    private final Integer id;
    private final String username;
    private String passwordHash;
    private final int role;
    private LocalDate lastLogin;
    private final byte[] salt;

    public Utente(Integer id, String username, String passwordHash, byte[] salt, int role, LocalDate lastLogin) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.lastLogin = lastLogin;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getRole() {
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

    public boolean checkPassword(String passwordHash) {
        return this.passwordHash.equals(passwordHash);
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public boolean isFirstLogin() {
        return this.lastLogin == null;
    }
}
