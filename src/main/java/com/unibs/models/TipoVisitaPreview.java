package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class TipoVisitaPreview extends TipoVisitaCore {
    private final String nomeLuogo;

    public TipoVisitaPreview(Integer id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti, int numMaxPartecipanti, PuntoIncontro puntoIncontro, String nomeLuogo) {
        super(id, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, puntoIncontro);
        this.nomeLuogo = nomeLuogo;
    }

    public TipoVisitaPreview(TipoVisitaCore tvc, String nomeLuogo) {
        this(tvc.getId(), tvc.getTitolo(), tvc.getDescrizione(), tvc.getDataInizio(), tvc.getDataFine(), tvc.getOraInizio(), tvc.getDurataMinuti(), tvc.isEntrataLibera(), tvc.getNumMinPartecipanti(), tvc.getNumMaxPartecipanti(), tvc.getPuntoIncontro(), nomeLuogo);
    }

    public String getNomeLuogo() {
        return nomeLuogo;
    }
}
