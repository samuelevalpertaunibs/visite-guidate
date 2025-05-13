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

    public List<Visita> getVisiteFromArchivio() throws DatabaseException {
        try {
            return visitaDao.getVisiteFromArchivio();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite nell'archivio.", e);
            throw new DatabaseException("Errore durante il recupero delle visite nell'archivio.");
        }
    }
}
