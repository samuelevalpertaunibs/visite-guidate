package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record TipoVisita(int id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine,
                         LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti,
                         int numMaxPartecipanti, Luogo luogo, PuntoIncontro puntoIncontro, Set<Giorno> giorni,
                         Set<Volontario> volontari) {

    public Set<Giorno> getGiorniSettimana() {
        return giorni;
    }

    public int getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public int getDurataMinuti() {
        return durataMinuti;
    }

    public boolean isEntrataLibera() {
        return entrataLibera;
    }

    public int getNumMinPartecipanti() {
        return numMinPartecipanti;
    }

    public int getNumMaxPartecipanti() {
        return numMaxPartecipanti;
    }

    public Luogo getLuogo() {
        return luogo;
    }

    public PuntoIncontro getPuntoIncontro() {
        return puntoIncontro;
    }

    public Set<Giorno> getGiorni() {
        return giorni;
    }

    public Set<Volontario> getVolontari() {
        return volontari;
    }

    @Override
    public String toString() {
        return "TipoVisita{}";
    }
}