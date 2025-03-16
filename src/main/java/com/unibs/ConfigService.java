package com.unibs;

import com.unibs.models.Comune;
import com.unibs.models.Config;

import java.util.ArrayList;

/**
 * ConfigService
 */
public class ConfigService {

    public Config aggiungiComune(Comune comuneDaAggiungere) throws DatabaseException, IllegalArgumentException {
        Config config = getConfig();

        if (config == null) config = ConfigDao.initDefault();

        if (comuneDaAggiungere.getNome().isBlank() || comuneDaAggiungere.getProvincia().isBlank()
                || comuneDaAggiungere.getRegione().isBlank())
            throw new IllegalArgumentException("I campi non possono essere vuoti.");

        if (config != null && config.doesInclude(comuneDaAggiungere))
            throw new IllegalArgumentException("Il comune è gia presente.");

        if (config != null) {
            ArrayList<Comune> comuni = config.getAmbitoTerritoriale();
            comuni.add(comuneDaAggiungere);
            config = ConfigDao.aggiungiComune(comuneDaAggiungere);
        }
        return config;
    }

    public Config getConfig() {
        return ConfigDao.getConfig();
    }

    private boolean doesInclude(Comune comuneDaAggiungere) {
        return ConfigDao.doesInclude(comuneDaAggiungere.getNome(), comuneDaAggiungere.getProvincia(), comuneDaAggiungere.getRegione());
    }

    public Config initDefault() {
        return ConfigDao.initDefault();
    }
}
