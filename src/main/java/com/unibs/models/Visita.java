package com.unibs.models;

import com.unibs.utils.ElementoSelezionabile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Visita implements ElementoSelezionabile {
    private final TipoVisita tipoVisita;
    private final LocalDate dataSvolgimento;
    private final Volontario volontario;
    private final StatoVisita stato;

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

    @Override
    public String getPlaceHolder() {
        return tipoVisita.titolo() + " in data " + dataSvolgimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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

    public enum StatoVisita {
        PROPOSTA,
        CONFERMATA,
        EFFETTUATA,
        COMPLETA,
        CANCELLATA
    }
}
