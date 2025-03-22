package com.unibs.models;

/**
 * Comuni
 */
public final class Comune {
    private final int id;
    private final String nome;
    private final String regione;
    private final String provincia;

    public Comune(int id, String nome, String provincia, String regione) {
        this.id = id;
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

    public int getId() {
        return id;
    }
}
