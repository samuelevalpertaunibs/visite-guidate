package com.unibs.models;

public record PuntoIncontro(String indirizzo, String comune, String provincia) {

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getComune() {
        return comune;
    }

    public String getProvincia() {
        return provincia;
    }
}
