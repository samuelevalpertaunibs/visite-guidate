package com.unibs;

import com.unibs.models.Tuple;
import com.unibs.models.User;

public class MainController {

    private final UserService userService;
    private final View view;

    public MainController(UserService userService, View view) {
        this.userService = userService;
        this.view = view;
    }

    public void start() {
        User currentUser = null;

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
        Tuple<Boolean, User> tuple = new Tuple<Boolean, User>(null, null);
        boolean isRegistered;
        User currentUser = null;
        int attempts = 0;

        while (currentUser == null) {
            // view.clearScreen();
            if (attempts > 0)
                view.showMessage("Credenziali errate. Riprova.");

            view.showMessage("=== Login ===");
            String username = view.getInput("Inserisci username: ");
            String password = view.getInput("Inserisci password: ");

            tuple = userService.authenticate(username, password);
            isRegistered = tuple.getFirst();
            currentUser = tuple.getSecond();

            if (!isRegistered && currentUser != null) {
                registerUser(currentUser);
                currentUser = null;
            }

            attempts++;
        }
        return currentUser;
    }

    protected void registerUser(User user) {
        view.clearScreen();
        view.showMessage("=== Cambio password ===");
        String newPassword = view.getInput("Inserisci la nuova password: ");

        try {
            userService.changePassword(user, newPassword);
            view.showMessage("Password cambiata con successo!");
        } catch (IllegalArgumentException e) {
            view.showMessage("Errore: " + e.getMessage());
        } catch (DatabaseException e) {
            view.showMessage("Errore: " + e.getMessage());
        }
    }

}
