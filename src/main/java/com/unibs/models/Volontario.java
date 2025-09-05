package com.unibs.models;

import com.unibs.utils.ElementoSelezionabile;

import java.time.LocalDate;

public class Volontario extends Utente implements ElementoSelezionabile {
    private static final int ruolo = 2;

    public Volontario(Integer id, String username, String passwordHash, byte[] salt, LocalDate lastLogin) {
        super(id, username, passwordHash, salt, ruolo, lastLogin);
    }

    public Volontario(Utente utente) {
        super(utente.getId(), utente.getUsername(), utente.getUsername(), utente.getSalt(), 2, utente.getLastLogin());
    }

    @Override
    public String getPlaceHolder() {
        return getUsername();
    }
}
