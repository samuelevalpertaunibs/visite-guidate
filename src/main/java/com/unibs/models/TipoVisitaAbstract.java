package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class TipoVisitaAbstract {
    private final Integer id;
    private final String titolo;
    private final String descrizione;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final LocalTime oraInizio;
    private final int durataMinuti;
    private final boolean entrataLibera;
    private final int numMinPartecipanti;
    private final int numMaxPartecipanti;
    private PuntoIncontro puntoIncontro;

    protected TipoVisitaAbstract(int id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine,
                                 LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti,
                                 int numMaxPartecipanti, PuntoIncontro puntoIncontro) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.oraInizio = oraInizio;
        this.durataMinuti = durataMinuti;
        this.entrataLibera = entrataLibera;
        this.numMinPartecipanti = numMinPartecipanti;
        this.numMaxPartecipanti = numMaxPartecipanti;
        this.puntoIncontro = puntoIncontro;
    }

    public Integer getId() {
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

    public PuntoIncontro getPuntoIncontro() {
        return puntoIncontro;
    }
}
