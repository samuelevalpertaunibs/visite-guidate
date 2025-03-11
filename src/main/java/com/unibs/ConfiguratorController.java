package com.unibs;

import com.unibs.models.Config;
import com.unibs.models.User;

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
    }

    @Override
    public void showMenu() {

    }

}
