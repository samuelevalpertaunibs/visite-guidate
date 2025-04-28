package com.unibs.services;

import com.unibs.DatabaseException;
import com.unibs.daos.GiorniSettimanaDao;
import com.unibs.models.Giorno;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GiornoService {
    private static final Logger LOGGER = Logger.getLogger(GiornoService.class.getName());

    private List<Giorno> giorni;
    private final GiorniSettimanaDao giorniSettimanaDao;

    public GiornoService() {
        giorniSettimanaDao = new GiorniSettimanaDao();
    }

    public List<Giorno> getGiorni() throws DatabaseException {
        try {
            if (giorni == null) {
                giorni = giorniSettimanaDao.findAll();
            }
            return giorni;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del comune", e);
            throw new DatabaseException("Impossibile recuperare i giorni della settimana");
        }
    }
}