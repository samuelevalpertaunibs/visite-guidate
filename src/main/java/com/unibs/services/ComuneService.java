package com.unibs.services;

import com.unibs.DatabaseException;
import com.unibs.daos.ComuneDao;
import com.unibs.models.Comune;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComuneService {
    private static final Logger LOGGER = Logger.getLogger(ComuneService.class.getName());
    private final ComuneDao comuneDao;

    public ComuneService() {
        this.comuneDao = new ComuneDao();
    }

    // Non ritorna un Optional perchè viene sempre chiamato con un id valido
    public Comune findById(int id) {
        try {
            return comuneDao.findById(id)
                    .orElseThrow(() -> new DatabaseException("Comune con ID " + id + " non trovato."));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero del comune con ID " + id, e);
            throw new DatabaseException("Errore durante il recupero del comune dal database.", e);
        }
    }

    public ArrayList<Comune> findAll() {
        try {
            return comuneDao.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero di tutti i comuni", e);
            throw new DatabaseException("Errore durante il recupero dei comuni dal database.");
        }
    }

}
