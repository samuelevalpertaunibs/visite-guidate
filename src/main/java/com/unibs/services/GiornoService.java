package com.unibs.services;

import com.unibs.DatabaseException;
import com.unibs.daos.GiorniSettimanaDao;
import com.unibs.models.Giorno;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
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

    public Giorno fromDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> new Giorno(1, "Lunedì");
            case TUESDAY -> new Giorno(2, "Martedì");
            case WEDNESDAY -> new Giorno(3, "Mercoledì");
            case THURSDAY -> new Giorno(4, "Giovedì");
            case FRIDAY -> new Giorno(5, "Venerdì");
            case SATURDAY -> new Giorno(6, "Sabato");
            case SUNDAY -> new Giorno(7, "Domenica");
        };
    }

    public Set<Giorno> getByTipoVisitaId(int tipoVisitaId) throws DatabaseException {
        try {
            return giorniSettimanaDao.findByTipoVisitaId(tipoVisitaId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei giorni", e);
            throw new DatabaseException("Impossibile recuperare i giorni");

        }
    }
}