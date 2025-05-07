package com.unibs.models;

import com.unibs.utils.ElementoSelezionabile;

/**
 * Comuni
 */
public record Comune(int id, String nome, String provincia, String regione) implements ElementoSelezionabile {

    public boolean equals(Comune comune) {
        return this.nome.equalsIgnoreCase(comune.nome());
    }

    @Override
    public String toString() {
        return String.format("%s (%s), %s", nome, provincia, regione);
    }

    @Override
    public String getPlaceHolder() {
        return nome();
    }
}
