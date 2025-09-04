package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class TipoVisitaPreview extends TipoVisitaAbstract {
    private final String nomeLuogo;

    public TipoVisitaPreview(Integer id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti, int numMaxPartecipanti, PuntoIncontro puntoIncontro, String nomeLuogo) {
        super(id, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, puntoIncontro);
        this.nomeLuogo = nomeLuogo;
    }

    public String getNomeLuogo() {
        return nomeLuogo;
    }
}
