package com.unibs.services;

import com.unibs.daos.VisitaDao;
import com.unibs.models.Fruitore;
import com.unibs.models.Visita;
import com.unibs.utils.DatabaseException;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FruitoreService {
    private static final Logger LOGGER = Logger.getLogger(FruitoreService.class.getName());
    private final ConfigService configService;
    private final VisitaService visitaService;
    private final VisitaDao visitaDao;

    public FruitoreService(ConfigService configService, VisitaService visitaService) {
        this.configService = configService;
        this.visitaService = visitaService;
        this.visitaDao = new VisitaDao();
    }

    public int iscrivi(Fruitore fruitore, Visita visitaSelezionata, int numeroPersoneDaIscrivere) {
        try {
            if (numeroPersoneDaIscrivere > configService.getNumeroMax()) {
                throw new IllegalArgumentException("Attenzione non puoi iscrivere piu di " + configService.getNumeroMax() + " persone per singola prenotazione.");
            }
            if (numeroPersoneDaIscrivere < 1) {
                throw new IllegalArgumentException("Numero di persone non valido.");
            }
            int iscrizioniRimanenti = visitaService.getIscrizioniRimanentiById(visitaSelezionata.getId());
            if (numeroPersoneDaIscrivere > iscrizioniRimanenti){
                throw new IllegalArgumentException("Attenzione la visita è quasi al completo, restano solo " + iscrizioniRimanenti + " posti.");
            }
            Integer codiceUnivoco = visitaDao.iscrivi(fruitore.getId(), visitaSelezionata.getId(), numeroPersoneDaIscrivere);
            if (numeroPersoneDaIscrivere == iscrizioniRimanenti) {
                visitaDao.setStatoById(visitaSelezionata.getId(), Visita.StatoVisita.COMPLETA.name());
            }
            return codiceUnivoco;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'inserimento dell'iscrizione: ", e);
            throw new DatabaseException("Errore durante l'inserimento dell'iscrizione.");
        }
    }
}
