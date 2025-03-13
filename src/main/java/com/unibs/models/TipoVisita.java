package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class TipoVisita {
    private String nome;
    private String descrizione;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private LocalTime oraInizio;
    private int durataMinuti;
    private boolean entrataLibera;
    private int numMinPartecipanti;
    private int numMaxPartecipanti;
    private Luogo luogo;

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

    public String getDescrizione() {return descrizione;}

    public LocalDate getDataInizio() {return dataInizio;}

    public LocalDate getDataFine() {return dataFine;}

    public int getDurataMinuti() {return durataMinuti;}

    public boolean getEntrataLibera() {return entrataLibera;}

    public int getNumMinPartecipanti() {return numMinPartecipanti;}

    public int getNumMaxPartecipanti() {return numMaxPartecipanti;}

    public Luogo getLuogo() {return luogo;}
}