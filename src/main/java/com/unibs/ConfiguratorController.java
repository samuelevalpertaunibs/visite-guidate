package com.unibs;

import com.unibs.models.Comune;
import com.unibs.models.Config;
import com.unibs.models.User;

import java.util.ArrayList;

public class ConfiguratorController implements IUserController {

    ConfiguratorService confService;
    private final View view;
    private final User user;

    public ConfiguratorController(View currentView, User currentUser) {
        this.confService = new ConfiguratorService();
        this.view = currentView;
        this.user = currentUser;
    }

    @Override
    public void start() {
        view.clearScreen();
        view.showMessage("Accesso effettuato come CONFIGURATORE\nBenvenuto, " + user.getUsername() + "!");

        Config config = null;

        try {
            config = ConfigDao.getConfig();
        } catch (DatabaseException e) {
            view.showMessage(e.getMessage());
        }

        if (config == null) {
            initData();
        }

        showMenu();
    }

    private void initData() {
        ArrayList<Comune> ambitoTerritoriale = new ArrayList<>();
        int numero_max_iscrizioni;
        boolean continua = true;

        // Init ambiente territoriale
        do {
            view.clearScreen();
            view.showTitle("Inizializzazione corpo dati");
            boolean inputValido = true;

            do {
                String nome = view.getLimitedInput("Inserisci un comune da aggiungere all'ambito territoriale: ", 32);
                String provincia = view.getLimitedInput("Inserisci la provincia: ", 32);
                String regione = view.getLimitedInput("Inserisci la regione: ", 32);

                inputValido = !nome.isBlank() && !provincia.isBlank() && !regione.isBlank();
                if (!inputValido){
                    view.clearScreen("Tutti i campi sono obbligatori. Riprova\n");
                    continue;
                }
                Comune comuneDaAggiungere = new Comune(nome, provincia, regione);
                // Se il comune non è gia presente lo aggiungo all'ambito territoriale
                if (!ambitoTerritoriale.contains(comuneDaAggiungere)) {
                    ambitoTerritoriale.add(comuneDaAggiungere);
                    view.clearScreen("Comune aggiunto: " + comuneDaAggiungere);
                }
            }while (!inputValido);
            boolean avanti = view.confirmDefaultN("Vuoi aggiungere un altro comune?");

            // Se inserisce 'stop' e almeno un comune è presente
            if (!avanti) {
                view.clearScreen();
                view.showTitle("Inizializzazione corpo dati");
                mostraAmbitoTerritoriale(ambitoTerritoriale.toArray(new Comune[0]));

                if (view.confirmDefaultY("Confermi la tua scelta?")) {
                    // Se conferma finisco il ciclo
                    continua = false;
                    break;
                }
                // Se non conferma resetto l'ambito territoriale
                ambitoTerritoriale = new ArrayList<>();
                continue;
            }

        }while (continua);

        // Init numero massimo di persone
        continua = true;
        do {
            numero_max_iscrizioni = view.getInt("Inizializzazione corpo dati",
                    "Inserisci il numero massimo di persone che un fruitore " +
                            "può iscrivere a una iniziativa mediante singola iscrizione: ", 1);
            if (view.confirmDefaultY("Confermi la tua scelta?")) {
                // Se conferma finisco il ciclo
                continua = false;
            }
        } while (continua);

        Config config = new Config(ambitoTerritoriale.toArray(new Comune[0]), numero_max_iscrizioni);
        ambitoTerritoriale.clear();

        // Init tipo visita



    }

    private void mostraAmbitoTerritoriale(Comune[] ambitoTerritoriale) {
        view.showMessage("Hai impostato il seguente ambito territoriale: ");
        for (Comune comune : ambitoTerritoriale) {
            view.showMessage(" - " + comune.toString());
        }
    }

    @Override
    public void showMenu() {
            throw new UnsupportedOperationException("TODO: showMenu");
    }

}
