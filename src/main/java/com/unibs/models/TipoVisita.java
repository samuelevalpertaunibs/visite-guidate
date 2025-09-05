package com.unibs.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class TipoVisita extends TipoVisitaCore {
    private final Luogo luogo;
    private final Set<Giorno> giorni;
    private final Set<CoppiaIdUsername> volontari;


    public TipoVisita(Integer id, String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine, LocalTime oraInizio, int durataMinuti, boolean entrataLibera, int numMinPartecipanti, int numMaxPartecipanti, PuntoIncontro puntoIncontro, Luogo luogo, Set<Giorno> giorni, Set<CoppiaIdUsername> volontari) {
        super(id, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, puntoIncontro);
        this.luogo = luogo;
        this.giorni = giorni;
        this.volontari = volontari;
    }

    public TipoVisita(TipoVisitaCore tvc, Luogo luogo, Set<Giorno> giorni, Set<CoppiaIdUsername> volontari) {
        this(tvc.getId(), tvc.getTitolo(), tvc.getDescrizione(), tvc.getDataInizio(), tvc.getDataFine(), tvc.getOraInizio(), tvc.getDurataMinuti(), tvc.isEntrataLibera(), tvc.getNumMinPartecipanti(), tvc.getNumMaxPartecipanti(), tvc.getPuntoIncontro(), luogo, giorni, volontari);
    }

    public Set<Giorno> getGiorniSettimana() {
        return giorni;
    }

    public Set<CoppiaIdUsername> getVolontari() {
        return volontari;
    }

    public Luogo getLuogo() {
        return luogo;
    }

}