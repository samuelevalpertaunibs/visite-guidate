package com.unibs.models;

public class Fruitore extends Utente {

    public Fruitore(Utente utente) {
        super(utente.getId(), utente.getUsername(), utente.getUsername(), utente.getSalt(), 3, utente.getLastLogin());
    }

}
