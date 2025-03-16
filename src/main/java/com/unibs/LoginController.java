package com.unibs;

import com.unibs.models.User;
import com.googlecode.lanterna.gui2.*;


public class LoginController {

    private final LoginService loginService;
    private final LoginView view;
    private final MultiWindowTextGUI gui;

    public LoginController(MultiWindowTextGUI gui) {
        this.loginService = new LoginService();
        this.view = new LoginView(this);
        this.gui = gui;
    }

    public Window getView() {
        return this.view.creaFinestra();
    }

    private void handleSpecificUser(User currentUser) {
        if (currentUser.getRole().equals("CONF")) {
            ConfiguratorController configuratorController = new ConfiguratorController(view, currentUser);
            configuratorController.start();
        } else {
            view.clearScreen("Ruolo non riconosciuto: accesso negato.");
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
                CambioPasswordView cambioPasswordView = new CambioPasswordView(this, user);
                gui.addWindowAndWait(cambioPasswordView.creaFinestra());
                view.resetLogin();
                return;
            } else {
                loginService.updateLastLogin(user);
                handleSpecificUser(user);
            }

            return;

        } catch (DatabaseException e) {
            return;
        }
    }

    protected void updatePassword(User user, String newPassword) {
        loginService.updatePassword(user, newPassword);
    }


    public WindowBasedTextGUI getGui() {
        return this.gui;
    }
}
