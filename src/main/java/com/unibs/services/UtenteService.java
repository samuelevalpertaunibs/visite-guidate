package com.unibs.services;

import com.unibs.DatabaseException;
import com.unibs.daos.UtenteDao;
import com.unibs.models.Volontario;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtenteService {
    private static final Logger LOGGER = Logger.getLogger(UtenteService.class.getName());
    UtenteDao utenteDao = new UtenteDao();

    public List<Volontario> findAllVolontari() throws DatabaseException {
        try {
            return utenteDao.getAllVolontari();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta della data preclusa", e);
            throw new DatabaseException("Errore nel recupero dei volontari");
        }
    }

    /*
    public List<LocalDate> getDateSelezionabiliPerVolontario (int volontarioId) {
        // 1. seleziono tutti i tipi di visita che hanno almeno un giorno compreso nel mese prossimo legati al volontario
        // 2. itero tutte le visite e aggiungo i giorni guardando la tabella NN
        // 3. ordino i giorni
        List<LocalDate> dateSelezionabili = new ArrayList<>();

        // 1. Seleziono solo gli id, il resto non mi serve
        LocalDate now = LocalDate.now();
        int mese = now.getDayOfMonth() > 15 ? now.getMonthValue() + 1 : now.getMonthValue();

        List<TipoVisita> tipiVisita = TipoVisitaDao.getByVolontarioIdAndMese(volontarioId, mese);

        for (Integer id : tipiVisitaIds) {

        }

        return dateSelezionabili;
    }
    */

}
