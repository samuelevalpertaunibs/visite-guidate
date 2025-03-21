package com.unibs.controllers;

import com.unibs.services.LoginService;
import com.unibs.models.User;
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

    private void handleSpecificUser(User currentUser) {
        //TODO fix check with role name instead of == 1
        if (currentUser.getRole() == 1) {
            ConfiguratorController configuratorController = new ConfiguratorController(this.gui, currentUser);
            configuratorController.start();
        } else {
            //view.clearScreen("Ruolo non riconosciuto: accesso negato.");
        }
    }

    public void verificaCredenziali(String username, String password) {
        try {
            User user = loginService.authenticate(username, password);

            if (user == null) {
                view.showPopupMessage("Credenziali errate. Riprova");
                return;
            }

            if (user.isFirstLogin()) {
                cambioPasswordView = new CambioPasswordView(this, user);
                gui.addWindowAndWait(cambioPasswordView.creaFinestra());
                view.resetLogin();
            } else {
                loginService.updateLastLogin(user);
                handleSpecificUser(user);
            }
        } catch (Exception e) {
            view.showErrorMessage(e.getMessage());
        }
    }

    public void updatePassword(User user, String newPassword, String newPasswordConfirm) {

        // Faccio i controlli nel controller in questo caso perchè è una cosa slegata dal serviceCambioPassword
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
            loginService.updatePassword(user, newPassword);
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
