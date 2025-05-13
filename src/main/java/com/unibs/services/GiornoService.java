package com.unibs.services;

import com.unibs.utils.DatabaseException;
import com.unibs.daos.GiorniDao;
import com.unibs.models.Giorno;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GiornoService {
    private static final Logger LOGGER = Logger.getLogger(GiornoService.class.getName());

    private List<Giorno> giorni;
    private final GiorniDao giorniDao;

    public GiornoService() {
        giorniDao = new GiorniDao();
    }

    public List<Giorno> getGiorni() throws DatabaseException {
        try {
            if (giorni == null) {
                giorni = giorniDao.findAll();
            }
            return giorni;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del comune", e);
            throw new DatabaseException("Impossibile recuperare i giorni della settimana");
        }
    }

}