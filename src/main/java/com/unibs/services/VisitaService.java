package com.unibs.services;

import com.unibs.daos.VisitaDao;
import com.unibs.models.*;
import com.unibs.utils.DatabaseException;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisitaService {
    private static final Logger LOGGER = Logger.getLogger(VisitaService.class.getName());
    private final VisitaDao visitaDao = new VisitaDao();

    public VisitaService() {
    }

    public List<Visita> getVisitePreviewByStato(Visita.StatoVisita stato) throws DatabaseException {
        try {
            return visitaDao.getVisitePreview(stato);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite", e);
            throw new DatabaseException("Impossibile recuperare le visite.");
        }
    }

    public void chiudiIscrizioneVisiteComplete() throws DatabaseException {
        try {
            List<Integer> idVisiteComplete = visitaDao.getIdVisiteCompleteDaChiudere();
            for (Integer idVisita : idVisiteComplete) {
                visitaDao.setStatoById(idVisita, Visita.StatoVisita.CONFERMATA.name());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la chiudura delle iscrizioni di visite complete.", e);
            throw new DatabaseException("Errore durante la chiusura delle iscrizioni di visite complete.");
        }
    }

    public void chiudiIscrizioneVisiteDaFare() throws DatabaseException {
        try {
            List<Integer> idVisiteDaFare = visitaDao.getIdVisiteDaFare();
            for (Integer idVisita : idVisiteDaFare) {
                visitaDao.setStatoById(idVisita, Visita.StatoVisita.CONFERMATA.name());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la chiudura delle iscrizioni di visite proposte con minimo iscritti raggiunto.", e);
            throw new DatabaseException("Errore durante la chiusura delle iscrizioni di visite proposte.");
        }
    }

    public void chiudiIscrizioneVisitaDaCancellare() throws DatabaseException {
        try {
            List<Integer> idVisiteDaCancellare = visitaDao.getIdVisiteDaCancellare();
            for (Integer idVisita : idVisiteDaCancellare) {
                visitaDao.setStatoById(idVisita, Visita.StatoVisita.CANCELLATA.name());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la chiudura delle iscrizioni di visite proposte con minimo iscritti non raggiunto.", e);
            throw new DatabaseException("Errore durante la chiusura delle iscrizioni di visite proposte.");
        }
    }

    public void generaVisiteEffettuate() {
        try {
            List<Integer> idVisiteDaRendereEffettuate = visitaDao.getIdVisiteDaRendereEffettuate();
            for (Integer idVisita : idVisiteDaRendereEffettuate) {
                visitaDao.archiviaVisita(idVisita);
                visitaDao.rimuoviById(idVisita);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante  la generazione delle visite effettuate.", e);
            throw new DatabaseException("Errore durante la generazione delle visite effettuate.");
        }
    }

    public void rimuoviVisiteCancellate() throws DatabaseException {
        try {
            visitaDao.rimuoviVisiteCancellate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione delle visite cancellate.", e);
            throw new DatabaseException("Errore durante la rimozione delle visite cancellate.");
        }
    }

    public List<Visita> getVisiteFromArchivio() throws DatabaseException {
        try {
            return visitaDao.getVisiteFromArchivio();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite nell'archivio.", e);
            throw new DatabaseException("Errore durante il recupero delle visite nell'archivio.");
        }
    }
}
