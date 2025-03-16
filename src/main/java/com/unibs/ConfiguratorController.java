package com.unibs;

import com.unibs.models.Config;
import com.unibs.models.User;

public class ConfiguratorController implements IUserController {

    private final ConfiguratorService configuratorService;
    private final View view;
    private final User user;
    private final ConfigService configService;

    public ConfiguratorController(View currentView, User currentUser) {
        this.configuratorService = new ConfiguratorService();
        this.configService = new ConfigService();
        this.view = currentView;
        this.user = currentUser;
    }

    @Override
    public void start() {
        view.clearScreen();
        view.showMessage("Accesso effettuato come CONFIGURATORE\nBenvenuto, " + user.getUsername() + "!");

        Config config = null;

        try {
            config = configService.getConfig();
        } catch (DatabaseException e) {
            view.showMessage(e.getMessage());
        }

        if (config == null || !config.getIsInitialized()) {
            initApp();
        }

        showMenu();
    }

    private void initApp() {
        ConfigController configController = new ConfigController(view);

        Config config = configController.initConfig();

        // Init tipo visita
        initData();


        // Se l'inizializzazione è andata a buon fine lo scrivo nel database
        config = configController.setIsInitialized(true);
    }

    private void initData() {
        TipoVisitaController tipoVisitaController = new TipoVisitaController(view);
        tipoVisitaController.aggiungiTipoVisita();
    }



    @Override
    public void showMenu() {
        throw new UnsupportedOperationException("TODO: showMenu");
    }

}
