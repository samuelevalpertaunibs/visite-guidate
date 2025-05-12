package com.unibs.models;

import com.unibs.services.VisitaService;
import com.unibs.utils.DatabaseException;

public class AppService {
    private final VisitaService visitaService;

    public AppService(VisitaService visitaService) {
        this.visitaService = visitaService;
    }

    public void eseguiOperazioniGiornaliere() throws DatabaseException {

        //Alla chiusura delle iscrizioni, cioè
        //tre giorni prima della data di svolgimento, una visita completa passa nello stato di visita
        //confermata
        visitaService.chiudiIscrizioneVisiteComplete();
        //e una visita proposta passa nello stato di visita confermata se il numero di
        //iscritti è maggiore o uguale al numero minimo di partecipanti mentre passa nello stato di
        //visita cancellata altrimenti.
        visitaService.chiudiIscrizioneVisiteDaFare();
        visitaService.chiudiIscrizioneVisitaDaCancellare();

        //Una visita che si trovi nello stato di visita confermata resta
        //tale fino al giorno di svolgimento della visita stessa (incluso), dopodiché essa passa nello
        //stato di visita effettuata e viene salvata nell’archivio storico. Una visita che si trovi nello
        //stato di visita cancellata resta tale fino al giorno del mancato svolgimento della visita
        //stessa (incluso), dopodiché essa cessa di essere memorizzata.
        visitaService.generaVisiteEffettuate();
        visitaService.rimuoviVisiteCancellate();

    }
}
