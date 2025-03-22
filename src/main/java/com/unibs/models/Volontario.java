package com.unibs.models;

import java.time.LocalDate;
//view specifica per quella visualizazione

public class Volontario extends User{

    private static final int ruolo = 2;

    public Volontario(String username, String passwordHash, byte[] salt, LocalDate lastLogin) {
        super(username, passwordHash, salt, ruolo, lastLogin);
    }

    public String getUsername() { return super.getUsername(); }
}
