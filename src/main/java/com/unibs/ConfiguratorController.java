package com.unibs;

import com.unibs.models.Config;
import com.unibs.models.User;

public class ConfiguratorController implements IUserController {

    private final ConfiguratorService configuratorService;
    private final View view;
    private final User user;

    public ConfiguratorController(View currentView, User currentUser) {
        this.configuratorService = new ConfiguratorService();
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

        // TODO: if (config.getInitializedCorrectly() == false) {
        if (config == null) {
            initApp();
        }

        showMenu();
    }

    private void initApp() {
        ConfigController configController = new ConfigController(view);
        Config config = configController.initConfig();

        // Init tipo visita
        initData(config);

    }

    private void initData(Config config) {
        //TODO
    }



    @Override
    public void showMenu() {
        throw new UnsupportedOperationException("TODO: showMenu");
    }

}
