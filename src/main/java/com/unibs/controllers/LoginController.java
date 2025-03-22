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
    CambioPasswordView cambioPasswordView;

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
        } else {
            view.showErrorMessage("Ruolo non riconosciuto");
        }
    }

    public void verificaCredenziali(String username, String password) {
        try {
            Utente utente = loginService.authenticate(username, password);

            if (utente == null) {
                view.showPopupMessage("Credenziali errate. Riprova");
                return;
            }

            if (utente.isFirstLogin()) {
                cambioPasswordView = new CambioPasswordView(this, utente);
                gui.addWindowAndWait(cambioPasswordView.creaFinestra());
                view.resetLogin();
            } else {
                utente = loginService.updateLastLogin(utente);
                handleSpecificUser(utente);
            }
        } catch (Exception e) {
            view.showErrorMessage(e.getMessage());
        }
    }

    public void updatePassword(Utente utente, String newPassword, String newPasswordConfirm) {

        // Faccio i controlli nel controller in questo caso perchè è una cosa slegata
        // dal serviceCambioPassword
        if (newPassword.isEmpty() || newPasswordConfirm.isEmpty()) {
            cambioPasswordView.mostraErrore("I campi non possono essere vuoti.");
            cambioPasswordView.resetCampi();
            return;
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            cambioPasswordView.mostraErrore("Le password non coincidono.");
            cambioPasswordView.resetCampi();
            return;
        }
        try {
            loginService.updatePassword(utente, newPassword);
            cambioPasswordView.close();
        } catch (Exception e) {
            cambioPasswordView.mostraErrore(e.getMessage());
            cambioPasswordView.resetCampi();
        }
    }

    public WindowBasedTextGUI getGui() {
        return this.gui;
    }
}
