package com.unibs.models;

public record PuntoIncontro(String indirizzo, String comune, String provincia) {
    public String toString() {
        return indirizzo + ", " + comune + " (" + provincia + ")";
    }
}
