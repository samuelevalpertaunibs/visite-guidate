package com.unibs;

import com.unibs.models.User;

public class MainController {

    private final UserService userService;
    private final View view;

    public MainController(UserService userService, View view) {
        this.userService = userService;
        this.view = view;
    }

    public void start() {
        User currentUser  = null;

        while (currentUser == null) {
            currentUser = authenticate();
        }

        handleSpecificUser(currentUser);
    }

    private void handleSpecificUser(User currentUser) {
        if (currentUser.getRole().equals("CONF")) {
            ConfiguratorController configuratorController = new ConfiguratorController(view, currentUser);
            configuratorController.start();
        } else {
            // TODO: ripetere il login
            view.showMessage("Ruolo non riconosciuto: accesso negato.");
        }
    }

    private User authenticate() {
        User currentUser = null;
        int attempts = 0;

        while (currentUser == null) {
            view.clearScreen();
            if (attempts > 0) view.showMessage("Credenziali errate. Riprova.");

            view.showMessage("=== Login ===");
            String username = view.getInput("Inserisci username: ");
            String password = view.getInput("Inserisci password: ");

            currentUser = userService.authenticate(username, password);
            attempts++;
        }

        return currentUser;
    }

}
