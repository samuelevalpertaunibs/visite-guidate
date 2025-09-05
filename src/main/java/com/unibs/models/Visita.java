package com.unibs.models;

import com.unibs.utils.ElementoSelezionabile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Visita implements ElementoSelezionabile {
    private final Integer id;
    private final TipoVisitaCore tipoVisita;
    private final LocalDate dataSvolgimento;
    private final CoppiaIdUsername volontario;
    private final StatoVisita stato;

    public Visita(Integer id, TipoVisitaCore tipoVisita, LocalDate dataSvolgimento, CoppiaIdUsername volontario, StatoVisita stato) {
        this.id = id;
        this.tipoVisita = tipoVisita;
        this.dataSvolgimento = dataSvolgimento;
        this.volontario = volontario;
        this.stato = stato;
    }

    public Visita(Integer id, TipoVisita tipoVisita, LocalDate dataSvolgimento,CoppiaIdUsername volontario) {
        this.id = id;
        this.tipoVisita = tipoVisita;
        this.dataSvolgimento = dataSvolgimento;
        this.volontario = volontario;
        this.stato = StatoVisita.PROPOSTA;
    }

    @Override
    public String getPlaceHolder() {
        return tipoVisita.getTitolo() + " in data " + dataSvolgimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public TipoVisitaCore getTipoVisita() {
        return tipoVisita;
    }

    public LocalDate getDataSvolgimento() {
        return dataSvolgimento;
    }

    public StatoVisita getStato() {
        return stato;
    }

    public CoppiaIdUsername getVolontario() {
        return volontario;
    }

    public Integer getId() {
        return id;
    }

    public enum StatoVisita {
        PROPOSTA, CONFERMATA, EFFETTUATA, COMPLETA, CANCELLATA
    }
}
