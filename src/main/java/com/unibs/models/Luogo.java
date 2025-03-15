package com.unibs.models;

public class Luogo {
    private final int id;
    private final String nome;
    private final String descrizione;
    private final Comune comune;

    public Luogo(int id, String nome, String descrizione, Comune comune) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.comune = comune;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return nome;
    }

    public String getDescription() {
        return descrizione;
    }

    public Comune getComune() {
        return comune;
    }

}
