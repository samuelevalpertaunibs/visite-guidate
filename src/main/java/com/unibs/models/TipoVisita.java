package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class TipoVisita {
    private final String nome;
    private final String descrizione;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final LocalTime oraInizio;
    private final int durataMinuti;
    private final boolean entrataLibera;
    private final int numMinPartecipanti;
    private final int numMaxPartecipanti;
    private final Luogo luogo;

    public TipoVisita(String nome, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera,int numMinPartecipanti, int numMaxPartecipanti, Luogo luogo) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.oraInizio = oraInizio;
        this.durataMinuti = durataMinuti;
        this.entrataLibera = entrataLibera;
        this.numMinPartecipanti = numMinPartecipanti;
        this.numMaxPartecipanti = numMaxPartecipanti;
        this.luogo = luogo;
    }

    public String getNome() {return nome;}

    public Luogo getLuogo() {return luogo;}
}