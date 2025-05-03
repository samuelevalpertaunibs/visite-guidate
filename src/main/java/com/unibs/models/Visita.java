package com.unibs.models;

import java.time.LocalDate;


public class Visita {
    private final TipoVisita tipoVisita;
    private final LocalDate dataSvolgimento;
    private final Volontario volontario;
    private final StatoVisita stato;

    public enum StatoVisita {
        PROPOSTA,
        CONFERMATA,
        EFFETTUATA,
        COMPLETA,
        CANCELLATA
    }

    public Visita(TipoVisita tipoVisita, LocalDate dataSvolgimento, Volontario volontario, StatoVisita stato) {
        this.tipoVisita = tipoVisita;
        this.dataSvolgimento = dataSvolgimento;
        this.volontario = volontario;
        this.stato = stato;
    }

    public Visita(TipoVisita tipoVisita, LocalDate dataSvolgimento, Volontario volontario) {
        this.tipoVisita = tipoVisita;
        this.dataSvolgimento = dataSvolgimento;
        this.volontario = volontario;
        this.stato = StatoVisita.PROPOSTA;
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

    public Volontario getVolontario() {
        return volontario;
    }
}
