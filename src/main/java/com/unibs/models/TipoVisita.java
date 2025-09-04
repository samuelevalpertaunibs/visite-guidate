package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class TipoVisita extends TipoVisitaAbstract {
    private final Luogo luogo;
    private final Set<Giorno> giorni;
    private final Set<Volontario> volontari;


    public TipoVisita(Integer id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti, int numMaxPartecipanti, Luogo luogo, PuntoIncontro puntoIncontro, Set<Giorno> giorni, Set<Volontario> volontari) {

        super(id, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, puntoIncontro);
        this.luogo = luogo;
        this.giorni = giorni;
        this.volontari = volontari;
    }

    public Set<Giorno> getGiorniSettimana() {
        return giorni;
    }

    public Set<Volontario> getVolontari() {
        return volontari;
    }

    public Luogo getLuogo() {
        return luogo;
    }

}