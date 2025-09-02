package com.unibs.models;

import com.unibs.utils.ElementoSelezionabile;

/**
 * Comuni
 */
public record Comune(int id, String nome, String provincia, String regione) implements ElementoSelezionabile {

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getRegione() {
        return regione;
    }

    public boolean equals(Comune comune) {
        return this.nome.equalsIgnoreCase(comune.nome());
    }

    @Override
    public String getPlaceHolder() {
        return nome();
    }

}
