package com.unibs.models;

import java.time.LocalDate;

public class Volontario extends User{
    private boolean aviability = true;

    public Volontario(String username, String passwordHash, byte[] salt, String role, LocalDate lastLogin, boolean aviability) {
        super(username, passwordHash, salt, role, lastLogin);
        this.aviability = aviability;
    }

    public void setAviability(boolean aviability) { this.aviability = aviability; }

    public boolean isAvailable() { return aviability; }
}