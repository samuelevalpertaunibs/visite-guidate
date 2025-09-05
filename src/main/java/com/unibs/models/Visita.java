package com.unibs.models;

import com.unibs.utils.ElementoSelezionabile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class Visita implements ElementoSelezionabile {
    private final Integer id;
    private final TipoVisitaCore tipoVisita;
    private final LocalDate dataSvolgimento;
    private final Map.Entry<Integer, String> volontario;
    private final StatoVisita stato;

    public Visita(Integer id, TipoVisitaCore tipoVisita, LocalDate dataSvolgimento, Map.Entry<Integer, String> volontario, StatoVisita stato) {
        this.id = id;
        this.tipoVisita = tipoVisita;
        this.dataSvolgimento = dataSvolgimento;
        this.volontario = volontario;
        this.stato = stato;
    }

    public Visita(Integer id, TipoVisita tipoVisita, LocalDate dataSvolgimento,  Map.Entry<Integer, String> volontario) {
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

    public Map.Entry getVolontario() {
        return volontario;
    }

    public Integer getId() {
        return id;
    }

    public enum StatoVisita {
        PROPOSTA, CONFERMATA, EFFETTUATA, COMPLETA, CANCELLATA
    }
}
