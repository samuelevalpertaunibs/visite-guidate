package com.unibs;

import com.unibs.models.User;

public class ConfiguratorController implements IUserController {

    private final View view;
    private final User user;
    private AmbitoTerritoriale ambitoTerritoriale;
    private List<Luogo> luoghi;

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

    public void aggiungiLuogo(Luogo luogo) {
        if (ambitoTerritoriale.contieneLuogo(luogo)) {
            luoghi.add(luogo);
            view.showMessage("Luogo valido e aggiunto con successo"); // Luogo valido e aggiunto
        }
        else view.showMessage("Il luogo inserito non rientra nell'ambito territoriale");
    }

    @Override
    public void showMenu() {

    }

}
