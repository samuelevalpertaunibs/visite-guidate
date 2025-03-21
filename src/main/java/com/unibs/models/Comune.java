package com.unibs.models;

/**
 * Comuni
 */
public final class Comune {
    private final String nome;
    private final String regione;
    private final String provincia;

    public Comune(String nome, String provincia, String regione) {
        this.nome = nome;
        this.regione = regione;
        this.provincia = provincia;
    }

    public boolean equals(Comune comune) {
        return this.nome.equalsIgnoreCase(comune.getNome());
    }

    public String getNome() {
        return nome;
    }

    public String getRegione() {
        return regione;
    }

    public String getProvincia() {
        return provincia;
    }

    @Override
    public String toString() {
        return String.format("%s (%s), %s", nome, provincia, regione);
    }

}
