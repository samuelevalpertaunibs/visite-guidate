package com.unibs.models;

public class Luogo {
    private String nome;
    private String descrizione;
    private Comune comune;

    public Luogo(String nome, String descrizione, Comune comune) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.comune = comune;
    }

    public String getName() { return nome; }

    public String getDescription() { return descrizione; }

    public Comune getComune() { return comune; }


}
