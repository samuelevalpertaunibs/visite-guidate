package com.unibs.models;

public class Luogo {
    private int id;
    private final String nome;
    private final String descrizione;
    private Comune comune;

    public Luogo(Integer id, String nome, String descrizione, Comune comune) {
        this.id = id == null ? -1 : id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.comune = comune;
    }

    public int getId() {
        return id;
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
                "Nome: %s\nDescrizione: %s\nComune: %s",
                nome, descrizione, comune.toString());
    }

    public void setComune(Comune comune) {
        this.comune = comune;
    }

    public int getComuneId() {
        return comune.id();
    }

    public void setId(int id) {
        this.id = id;
    }
}
