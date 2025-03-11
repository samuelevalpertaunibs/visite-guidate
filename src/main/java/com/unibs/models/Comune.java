package com.unibs.models;

/**
 * Comuni
 */
public final class Comune {
    private final String nome;
    private final String regione;
    private final String provincia;

    public Comune(String nome, String regione, String provincia) {
        this.nome = nome;
        this.regione = regione;
        this.provincia = provincia;
    }

    public boolean equals(Comune comune) {
        return (this.nome.equals(comune.getNome()) && this.regione.equals(comune.getRegione())
                && this.provincia.equals(comune.getProvincia()));
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
}
