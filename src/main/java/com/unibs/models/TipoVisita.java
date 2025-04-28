package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TipoVisita {
    private final int id;
    private final String titolo;
    private final String descrizione;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final LocalTime oraInizio;
    private int durataMinuti;
    private final boolean entrataLibera;
    private final int numMinPartecipanti;
    private final int numMaxPartecipanti;
    private Luogo luogo;
    private PuntoIncontro puntoIncontro;
    private List<Giorno> giorni;
    private List<Volontario> volontari;

    public TipoVisita(int id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera,int numMinPartecipanti, int numMaxPartecipanti, Luogo luogo, PuntoIncontro puntoIncontro, List<Giorno> giorni, List<Volontario> volontari) {
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
        this.giorni = giorni;
        this.volontari = volontari;
    }

    public TipoVisita(int id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti, int numMaxPartecipanti, PuntoIncontro puntoIncontro) {
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