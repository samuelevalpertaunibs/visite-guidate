package com.unibs.services;
import com.unibs.DatabaseException;
import com.unibs.daos.VisitaDao;
import com.unibs.models.Visita;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisitaService {
    private final VisitaDao visitaDao = new VisitaDao();
    private static final Logger LOGGER = Logger.getLogger(VisitaService.class.getName());

    public List<Visita> getVisitePreview(Visita.StatoVisita stato) throws DatabaseException {
        try {
            return visitaDao.getVisitePreview(stato);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite", e);
            throw new DatabaseException("Impossibile recuperare le visite.");
        }
    }
}
