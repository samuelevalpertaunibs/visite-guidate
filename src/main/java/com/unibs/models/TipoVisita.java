package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public record TipoVisita(int id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine,
                         LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti,
                         int numMaxPartecipanti, Luogo luogo, PuntoIncontro puntoIncontro, Set<Giorno> giorni,
                         Set<Volontario> volontari) {

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataInizioString = dataInizio.format(formatter);
        String dataFineString = dataFine.format(formatter);
        return "Titolo: " + titolo + "\n" +
                "Descrizione: " + descrizione + "\n" +
                "Data inizio: " + dataInizioString + "\n" +
                "Data fine: " + dataFineString + "\n" +
                "Ora inizio: " + oraInizio + "\n" +
                "Durata (minuti): " + durataMinuti + "\n" +
                "Entrata libera: " + (entrataLibera ? "Sì" : "No") + "\n" +
                "Numero minimo partecipanti: " + numMinPartecipanti + "\n" +
                "Numero massimo partecipanti: " + numMaxPartecipanti + "\n" +
                "Luogo: " + (luogo != null ? luogo.getNome() : "N/D") + "\n" +
                "Punto incontro: " + (puntoIncontro != null ? puntoIncontro.toString() : "N/D");
    }
}