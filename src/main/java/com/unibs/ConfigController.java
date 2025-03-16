package com.unibs;

import com.unibs.models.Comune;
import com.unibs.models.Config;

import java.util.ArrayList;

public class ConfigController {
    private final ConfigService configService;
    private final View view;

    protected ConfigController(View view) {
        this.configService = new ConfigService();
        this.view = view;
    }

    protected Config initConfig() {
        Config config;
        do {
            // Resetto le Config se il configuratore non ha confermato l'aggiunta
            config = configService.initDefault();

            do {
                view.clearScreenAndShowTitle("Inizializzazione dell'ambito territoriale");
                String nome = view.getLimitedInput(
                        "Inserisci un comune da aggiungere all'ambito territoriale: ", 32);
                String provincia = view.getLimitedInput("Inserisci la provincia: ", 32);
                String regione = view.getLimitedInput("Inserisci la regione: ", 32);
                Comune comuneDaAggiungere = new Comune(nome, regione, provincia);

                try {
                    config = configService.aggiungiComune(comuneDaAggiungere);
                } catch (Exception e) {
                    view.clearScreen("Errore: " + e.getMessage());
                }

            } while (config == null || config.isAmbitoTerritorialeVuoto() || view.getYesOrNoWithDefaultNo("Vuoi inserire un'altro comune?"));

            // Ripeto il ciclo se il configuratore non conferma
            view.clearScreen();
            mostraAmbitoTerritoriale(config.getAmbitoTerritoriale());
        } while (!view.getConfirm());

        // Inizializzazione numero max persone
        do {
            int numero_max = view.getInt("Inizializzazione di configurazioni aggiuntive", "Inserisci il numero massimo di persone che un fruitore può iscrivere a una iniziativa mediante una singola iscrizione: ", 1);
            config = ConfigDao.setNumeroMax(numero_max);
        } while (!view.getConfirm());

        return config;
    }

    private void mostraAmbitoTerritoriale(ArrayList<Comune> ambitoTerritoriale) {
        view.showMessage("Hai impostato il seguente ambito territoriale: ");
        for (Comune comune : ambitoTerritoriale) {
            view.showMessage(" - " + comune.toString());
        }
    }

    public Config setIsInitialized(boolean isInitialized) {
        return ConfigDao.setIsInitialized(isInitialized);
    }
}
