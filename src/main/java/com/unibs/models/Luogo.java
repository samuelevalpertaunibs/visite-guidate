package com.unibs.models;

public class Luogo {
    private final String nome;
    private final String descrizione;
    private final Comune comune;

    public Luogo(String nome, String descrizione, Comune comune) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.comune = comune;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Comune getComune() {
        return comune;
    }

    @Override
    public String toString() {
        return String.format(
                "%s, %s\n\tSituato a %s.",
                nome, descrizione, comune.toString()
        );
    }

    public String getNomeComune() {
        return comune.getNome();
    }
}
