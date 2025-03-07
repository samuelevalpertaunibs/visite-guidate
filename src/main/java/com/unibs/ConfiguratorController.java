package com.unibs;

import com.unibs.models.User;

public class ConfiguratorController implements UserController {

    private final View view;
    private final User user;

    public ConfiguratorController(View currentView, User currentUser) {
        this.view = currentView;
        this.user = currentUser;
    }

    @Override
    public void start() {
        view.clearScreen();
        view.showMessage("Accesso effettuato come CONFIGURATORE\nBenvenuto, " + user.getUsername() + "!" );
        showMenu();
    }

	@Override
    public void showMenu() {
        
    }

}
