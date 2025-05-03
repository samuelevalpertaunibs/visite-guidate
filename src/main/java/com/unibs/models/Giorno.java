package com.unibs.models;

import com.unibs.utils.SelezionabileConCheckbox;

public record Giorno(int id, String nome) implements SelezionabileConCheckbox {

    @Override
    public String getPlaceHolder() {
        return nome;
    }


    public int getId() {
        return id;
    }
}
