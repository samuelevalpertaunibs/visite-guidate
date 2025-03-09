package com.unibs;

import com.unibs.models.User;

public class LoginController {

    private final LoginService loginService;
    private final View view;

    public LoginController(LoginService loginService, View view) {
        this.loginService = loginService;
        this.view = view;
    }

    public void start() {
        User currentUser = null;

        view.clearScreen();
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
            view.clearScreen("Ruolo non riconosciuto: accesso negato.");
            start();
        }
    }

    private User authenticate() {
        User currentUser = null;

        while (currentUser == null) {
            view.clearScreen();
            view.showTitle("Login");
            String username = view.getInput("Inserisci username: ");
            String password = view.getInput("Inserisci password: ");

            try {
                currentUser = loginService.authenticate(username, password);
            } catch (DatabaseException | IllegalArgumentException e) {
                view.clearScreen("Errore: " + e.getMessage());
                continue;
            }

            if (currentUser == null) {
                view.clearScreen("Credenziali errate. Riprova.");
                continue;
            }

            if (currentUser.getLastLogin() != null) {
                try {
                    currentUser = loginService.updateLastLogin(currentUser.getUsername());
                } catch (DatabaseException e) {
                    view.clearScreen("Errore: " + e.getMessage());
                    currentUser = null;
                    continue;
                }
            }

            if (currentUser != null && currentUser.getLastLogin() == null) {
                registerUser(currentUser);
                currentUser = null;
            }
        }

        return currentUser;

    }

    protected void registerUser(User user) {
        view.clearScreen();
        view.showTitle("Cambio password");
        String newPassword = view.getInput("Inserisci la nuova password: ");

        try {
            loginService.registerUser(user, newPassword);
            view.clearScreen("Password cambiata con successo!");
        } catch (DatabaseException | IllegalArgumentException e) {
            view.clearScreen("Errore: " + e.getMessage());
            registerUser(user);
        }
    }

}
