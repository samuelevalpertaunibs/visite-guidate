package com.unibs.services;

import com.unibs.utils.DatabaseException;
import com.unibs.daos.ConfigDao;
import com.unibs.models.Comune;
import com.unibs.models.Config;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConfigService
 */
public class ConfigService {
    private final ConfigDao configDao;
    private final Logger LOGGER = Logger.getLogger(ConfigService.class.getName());

    public ConfigService() {
        this.configDao = new ConfigDao();
    }

    public Config getConfig() throws DatabaseException {
        try {
            Config config = configDao.getConfig()
                    .orElseThrow(() -> new DatabaseException("Configurazione non trovata."));

            List<Comune> ambitoTerritoriale = configDao.getAmbitoTerritoriale();
            config.setAmbitoTerritoriale(ambitoTerritoriale);

            return config;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dell'ambito territoriale", e);
            throw new DatabaseException("Ambito territoriale non trovato.");
        }
    }

    public void aggiungiComune(String nome, String provincia, String regione) throws DatabaseException, IllegalArgumentException {
        if (nome.isBlank() || provincia.isBlank() || regione.isBlank())
            throw new IllegalArgumentException("I campi non possono essere vuoti.");

        Config config = getConfig();
        Comune comuneDaAggiungere = new Comune(0, nome, provincia, regione);

        if (config.doesInclude(comuneDaAggiungere))
            throw new IllegalArgumentException("Il comune è gia presente.");

        try {
            configDao.aggiungiComune(comuneDaAggiungere);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del comune", e);
            throw new DatabaseException("Impossibile inserire il comune.");

        }
    }

    public void initDefault() throws DatabaseException {
        try {
            configDao.initDefault();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'inizializzazione delle configurazioni di default", e);
            throw new DatabaseException("Errore durante l'inizializzazione della configurazione.");
        }
    }

    public boolean isInitialized() {
        try {
            Config config = getConfig();
            return config.getPeriodoCorrente() != null;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public int setNumeroMaxPersone(String numeroMaxPersone) throws DatabaseException {
        try {
            int numeroMax = Integer.parseInt(numeroMaxPersone);
            if (numeroMax < 1 || numeroMax > 100)
                throw new IllegalArgumentException("Il numero max deve essere positivo e minore di 100.");
            configDao.setNumeroMax(numeroMax);
            return numeroMax;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il numero massimo di persone non è valido.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la modifica della configurazione", e);
            throw new DatabaseException("Errore durante la modifica del numero massimo di persone.");
        }

    }

    public boolean esisteAlmenoUnComune() {
        try {
            Config config = getConfig();
            return config.getNumeroComuni() > 0;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public void setPeriodoCorrente(LocalDate periodoCorrente) throws DatabaseException {
        try {
            configDao.setPeriodoCorrente(periodoCorrente);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la modifica della configurazione", e);
            throw new DatabaseException("Errore durante la modifica della configurazione.");
        }
    }

    public boolean regimeAttivo() {
        try {
            LocalDate periodoCorrente = getConfig().getPeriodoCorrente();
            if (periodoCorrente == null)
                return false;

            // true -> oggi >= inizioPeriodoCorrente
            return !LocalDate.now().isBefore(periodoCorrente);
        } catch (DatabaseException e) {
            return false;
        }
    }

    public List<Comune> getAmbitoTerritoriale() throws DatabaseException {
        return getConfig().getAmbitoTerritoriale();
    }

    public int getNumeroMax() {
        return getConfig().getNumeroMassimoIscrizioniPrenotazione();
    }

    public YearMonth getMesePeriodoCorrente() throws DatabaseException {
        return YearMonth.from(getConfig().getPeriodoCorrente());
    }
}
