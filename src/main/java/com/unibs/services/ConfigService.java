package com.unibs.services;

import com.unibs.DatabaseException;
import com.unibs.daos.ConfigDao;
import com.unibs.models.Comune;
import com.unibs.models.Config;

import java.util.ArrayList;

/**
 * ConfigService
 */
public class ConfigService {

    public void aggiungiComune(Comune comuneDaAggiungere) throws DatabaseException, IllegalArgumentException {
        Config config = getConfig();
        if (config == null) {
            throw new DatabaseException("Config non trovata");
        }

        if (comuneDaAggiungere.getNome().isBlank() || comuneDaAggiungere.getProvincia().isBlank()
                || comuneDaAggiungere.getRegione().isBlank())
            throw new IllegalArgumentException("I campi non possono essere vuoti.");

        if (config != null && config.doesInclude(comuneDaAggiungere))
            throw new IllegalArgumentException("Il comune è gia presente.");

        if (config != null) {
            ConfigDao.aggiungiComune(comuneDaAggiungere);
        }
    }

    public Config getConfig() {
        return ConfigDao.getConfig();
    }

    private boolean doesInclude(Comune comuneDaAggiungere) {
        return ConfigDao.doesInclude(comuneDaAggiungere.getNome(), comuneDaAggiungere.getProvincia(), comuneDaAggiungere.getRegione());
    }

    public void initDefault() {
        ConfigDao.initDefault();
    }

    public boolean isInitialized() {
        Config config = ConfigDao.getConfig();
        if (config == null)
            return false;
        return config.getIsInitialized();
    }

    public void setNumeroMaxPersone(String numeroMaxPersone) {
        try {
            int numeroMax = Integer.parseInt(numeroMaxPersone);
            if (numeroMax < 1 || numeroMax > 100)
                throw new IllegalArgumentException("Il numero max deve essere positivo e minore di 100.");
            ConfigDao.setNumeroMax(numeroMax);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il numero massimo di persone non è valido.");
        }

    }

    public boolean esisteAlmenoUnComune() {
        return ConfigDao.getNumeroComuni() > 0;
    }

    public ArrayList<Comune> getAmbitoTerritoriale() {
        return ConfigDao.getAmbitoTerritoriale();
    }
}
