package com.unibs;

import com.unibs.models.User;

import java.util.List;

public class ConfiguratorController implements IUserController {

    private final View view;
    private final User user;

    public ConfiguratorController(View currentView, User currentUser) {
        this.view = currentView;
        this.user = currentUser;
    }

    @Override
    public void start() {
        view.clearScreen();
        view.showMessage("Accesso effettuato come CONFIGURATORE\nBenvenuto, " + user.getUsername() + "!");
        showMenu();
    }

    @Override
    public void showMenu() {
        String[] options = new String[]{"Aggiungi Luogo",
                "Aggiungi date precluse per il mese i+3",
                "Aggiungi tipo di visita",
                "Modifica numero massimo per iscrizione",
                "Visualizza elenco delle visite per stato",
                "Visualizza elenco tipi visita associati a ciascun luogo",
                "Visualizza elenco volontari",
                "Visualizza numero massimo per iscrizione"};
        view.showMenu(options);
    }

}
