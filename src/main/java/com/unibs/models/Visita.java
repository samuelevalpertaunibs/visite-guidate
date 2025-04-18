package com.unibs.models;

import java.time.LocalDate;


public class Visita {
    private final int id;
    private final TipoVisita tipoVisita;
    private final LocalDate dataSvolgimento;
    private final StatoVisita stato;

    public enum StatoVisita {
        PROPOSTA,
        CONFERMATA,
        EFFETTUATA,
        COMPLETA,
        CANCELLATA
    }

    public Visita(int id, TipoVisita tipoVisita, LocalDate dataSvolgimento, StatoVisita stato) {
        this.id = id;
        this.tipoVisita = tipoVisita;
        this.dataSvolgimento = dataSvolgimento;
        this.stato = stato;
    }

    public int getId() {
        return id;
    }

    public TipoVisita getTipoVisita() {
        return tipoVisita;
    }

    public LocalDate getDataSvolgimento() {
        return dataSvolgimento;
    }

    public StatoVisita getStato() {
        return stato;
    }
}
