package com.unibs.controllers;

import com.unibs.services.LoginService;
import com.unibs.models.Utente;
import com.googlecode.lanterna.gui2.*;
import com.unibs.views.CambioPasswordView;
import com.unibs.views.LoginView;

public class LoginController {

    private final LoginService loginService;
    private final LoginView view;
    private final MultiWindowTextGUI gui;
    private CambioPasswordView cambioPasswordView;

    public LoginController(MultiWindowTextGUI gui) {
        this.loginService = new LoginService();
        this.view = new LoginView(this);
        this.gui = gui;
    }

    public Window getView() {
        return this.view.creaFinestra();
    }

    private void handleSpecificUser(Utente currentUtente) {
        view.resetLogin();
        // TODO fix check with role name instead of == 1
        if (currentUtente.getRole() == 1) {
            ConfiguratorController configuratorController = new ConfiguratorController(this.gui, currentUtente);
            configuratorController.start();
        } else if (currentUtente.getRole() == 2) {
            VolontarioController volontarioController = new VolontarioController(this.gui, currentUtente);
            volontarioController.start();
        } else {
            view.mostraErrore("Ruolo non riconosciuto");
        }
    }

    public void verificaCredenziali(String username, String password) {
        try {
            Utente utente = loginService.authenticate(username, password);

            if (utente == null) {
                view.resetLogin();
                view.mostraErrore("Credenziali errate");
                return;
            }

            if (utente.isFirstLogin()) {
                view.close();
                cambioPasswordView = new CambioPasswordView(this, utente);
                gui.addWindowAndWait(cambioPasswordView.creaFinestra());
                gui.addWindowAndWait(view.creaFinestra());
            } else {
                view.close();
                utente = loginService.updateLastLogin(utente);
                handleSpecificUser(utente);
                gui.addWindowAndWait(view.creaFinestra());
            }
        } catch (Exception e) {
            view.resetLogin();
            view.mostraErrore(e.getMessage());
        }
    }

    public void updatePassword(Utente utente, String newPassword, String newPasswordConfirm) {
        // Faccio i controlli nel controller in questo caso perchè è una cosa slegata dal serviceCambioPassword
        if (newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
            cambioPasswordView.resetCampi();
            cambioPasswordView.mostraErrore("I campi non possono essere vuoti");
            return;
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            cambioPasswordView.resetCampi();
            cambioPasswordView.mostraErrore("Le password non coincidono");
            return;
        }
        try {
            loginService.updatePassword(utente, newPassword);
            cambioPasswordView.close();
        } catch (Exception e) {
            cambioPasswordView.resetCampi();
            cambioPasswordView.mostraErrore(e.getMessage());
        }
    }

}
