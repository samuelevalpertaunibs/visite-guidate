package com.unibs.services;

import com.unibs.DatabaseException;
import com.unibs.daos.ConfigDao;
import com.unibs.models.Comune;
import com.unibs.models.Config;

import java.time.LocalDate;
import java.util.List;

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

        if (config.doesInclude(comuneDaAggiungere))
            throw new IllegalArgumentException("Il comune è gia presente.");

        ConfigDao.aggiungiComune(comuneDaAggiungere);
    }

    public Config getConfig() {
        return ConfigDao.getConfig();
    }

    public void initDefault() {
        ConfigDao.initDefault();
    }

    public boolean isInitialized() {
        Config config = ConfigDao.getConfig();
        if (config == null)
            return false;
        return config.getInitializedOn() != null;
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

    public List<Comune> getAmbitoTerritoriale() {
        return ConfigDao.getAmbitoTerritoriale();
    }

    public void setInitializedOn(LocalDate initializedOn) {
        ConfigDao.setInitializedOn(initializedOn);
    }

    public int getNumeroMaxPersone() {
        return ConfigDao.getNumeroMax();
    }

    public boolean regimeAttivo() {
        Config config = ConfigDao.getConfig();
        if (config == null)
            return false;
        if (config.getInitializedOn() == null)
            return false;

        LocalDate initializedOn = config.getInitializedOn();
        LocalDate attivazioneRegime = initializedOn.withDayOfMonth(16);
        if (initializedOn.getDayOfMonth() > 16)
            attivazioneRegime = attivazioneRegime.plusMonths(1);

        return !LocalDate.now().isBefore(attivazioneRegime);
    }
}
