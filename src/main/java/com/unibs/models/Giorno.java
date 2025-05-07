package com.unibs.models;

import com.unibs.utils.ElementoSelezionabile;

public record Giorno(int id, String nome) implements ElementoSelezionabile {

    @Override
    public String getPlaceHolder() {
        return nome;
    }


    public int getId() {
        return id;
    }
}
