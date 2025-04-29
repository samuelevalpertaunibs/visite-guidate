package com.unibs.controllers;

import com.unibs.services.*;
import com.unibs.models.Utente;
import com.googlecode.lanterna.gui2.*;
import com.unibs.views.CambioPasswordView;
import com.unibs.views.LoginView;

public class LoginController {

    private final ServiceFactory serviceFactory;
    private final LoginService loginService;
    private LoginView view;
    private final MultiWindowTextGUI gui;
    private CambioPasswordView cambioPasswordView;
    private Utente utente;

    public LoginController(MultiWindowTextGUI gui, ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        this.loginService = serviceFactory.getLoginService();
        this.gui = gui;
    }

    private void handleSpecificUser(Utente currentUtente) {
        this.utente = currentUtente;
        if (currentUtente.getRole() == 1) {
            inizializzaConfiguratore();
        } else if (currentUtente.getRole() == 2) {
            inizializzaVolontario();
        } else {
            view.mostraErrore("Ruolo non riconosciuto.");
        }
    }

    private void inizializzaConfiguratore() {
        ConfiguratoreController configuratoreController = new ConfiguratoreController(gui, utente, serviceFactory);
        configuratoreController.start();
    }

    private void inizializzaVolontario() {
        VolontarioController volontarioController = new VolontarioController(gui, utente, serviceFactory);
        volontarioController.start();
    }

    public void apriLogin(MultiWindowTextGUI gui) {
        view = new LoginView();
        view.getLoginButton().addListener(this::verificaCredenziali);
        view.mostra(gui);
    }

    private void verificaCredenziali(Button button) {
        String username = view.getUsername();
        String password = view.getPassword();
        try {
            utente = loginService.autentica(username, password);
            if (utente == null) {
                view.resetLogin();
                view.mostraErrore("Credenziali errate.");
            } else {
                view.chiudi();
                if (utente.isFirstLogin()) {
                    cambioPasswordView = new CambioPasswordView();
                    initListenerCambioPasswordView();
                    cambioPasswordView.mostra(gui);
                } else {
                    utente.setLastLogin(loginService.updateLastLogin(utente));
                    handleSpecificUser(utente);
                }
                apriLogin(gui);
            }
        } catch (Exception e) {
            view.mostraErrore(e.getMessage());
        }
    }

    private void initListenerCambioPasswordView() {
        cambioPasswordView.getConfermaButton().addListener(this::updatePassword);
    }

    private void updatePassword(Button button) {
        cambioPasswordView.mostraErrore("");
        String password = cambioPasswordView.getPassword();
        String confermaPassword = cambioPasswordView.getConfermaPassword();
        try {
            loginService.updatePassword(utente, password, confermaPassword);
            cambioPasswordView.chiudi();
            loginService.updateLastLogin(utente);
        } catch (Exception e) {
            cambioPasswordView.resetCampi();
            cambioPasswordView.mostraErrore(e.getMessage());
        }
    }
}
