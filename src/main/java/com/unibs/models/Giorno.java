package com.unibs.models;

public record Giorno(int id, String nome) {

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
