package com.unibs.models;

public class Luogo {
    private String nome;
    private Provincia provincia;
    private Regione regione;

    public Luogo(String nome, Provincia provincia, Regione regione) {
        this.nome = nome;
        this.provincia = provincia;
        this.regione = regione;
    }
    public Regione getRegione() {
        return regione;
    }

    public String getNome() {
        return nome;
    }

    public Provincia getProvincia() {
        return provincia;
    }

}