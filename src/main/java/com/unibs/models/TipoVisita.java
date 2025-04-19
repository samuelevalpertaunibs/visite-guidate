package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class TipoVisita {
    private final String titolo;
    private final String descrizione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private final LocalTime oraInizio;
    private int durataMinuti;
    private final boolean entrataLibera;
    private int numMinPartecipanti;
    private int numMaxPartecipanti;
    private Luogo luogo;
    private final PuntoIncontro puntoIncontro;

    public TipoVisita(String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera,int numMinPartecipanti, int numMaxPartecipanti, Luogo luogo, PuntoIncontro puntoIncontro) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.oraInizio = oraInizio;
        this.durataMinuti = durataMinuti;
        this.entrataLibera = entrataLibera;
        this.numMinPartecipanti = numMinPartecipanti;
        this.numMaxPartecipanti = numMaxPartecipanti;
        this.luogo = luogo;
        this.puntoIncontro = puntoIncontro;
    }

    public TipoVisita(String titolo, String descrizione, PuntoIncontro puntoIncontro, LocalTime oraInizio, boolean entrataLibera) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.puntoIncontro = puntoIncontro;
        this.oraInizio = oraInizio;
        this.entrataLibera = entrataLibera;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public LocalTime getOraInizio() {
        return oraInizio;
    }

    public boolean isEntrataLibera() {
        return entrataLibera;
    }

    public Luogo getLuogo() {
        return luogo;
    }

    public PuntoIncontro getPuntoIncontro() {
        return puntoIncontro;
    }
}