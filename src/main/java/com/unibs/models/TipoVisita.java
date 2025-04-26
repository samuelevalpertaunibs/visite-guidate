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
    private PuntoIncontro puntoIncontro;
    private final int id;

    public TipoVisita(int id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera,int numMinPartecipanti, int numMaxPartecipanti, Luogo luogo, PuntoIncontro puntoIncontro) {
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
        this.luogo = luogo;
        this.puntoIncontro = puntoIncontro;
    }

    public TipoVisita(int id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti, int numMaxPartecipanti) {
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

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public int getDurataMinuti() {
        return durataMinuti;
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

    @Override
    public String toString() {
        return "Titolo: " + titolo + "\n" +
                "Descrizione: " + descrizione + "\n" +
                "Data inizio: " + dataInizio + "\n" +
                "Data fine: " + dataFine + "\n" +
                "Ora inizio: " + oraInizio + "\n" +
                "Durata (minuti): " + durataMinuti + "\n" +
                "Entrata libera: " + (entrataLibera ? "Sì" : "No") + "\n" +
                "Numero minimo partecipanti: " + numMinPartecipanti + "\n" +
                "Numero massimo partecipanti: " + numMaxPartecipanti + "\n" +
                "Luogo: " + (luogo != null ? luogo.getNome() : "N/D") + "\n" +
                "Punto incontro: " + (puntoIncontro != null ? puntoIncontro.toString() : "N/D");
    }

}