package com.unibs.controllers;

import com.unibs.services.*;
import com.unibs.models.Utente;
import com.googlecode.lanterna.gui2.*;
import com.unibs.views.CambioPasswordView;
import com.unibs.views.LoginView;
import com.unibs.views.RegistrazioneView;

public class LoginController {

    private final ServiceFactory serviceFactory;
    private final LoginService loginService;
    private LoginView loginView;
    private final MultiWindowTextGUI gui;
    private CambioPasswordView cambioPasswordView;
    private RegistrazioneView registrazioneView;
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
            loginView.mostraErrore("Ruolo non riconosciuto.");
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
        loginView = new LoginView();
        loginView.getLoginButton().addListener(button1 -> verificaCredenziali());
        loginView.getRegistratiButton().addListener(button -> apriRegistrazione());
        loginView.mostra(gui);
    }

    private void apriRegistrazione() {
        registrazioneView = new RegistrazioneView();
        registrazioneView.getRegistratiButton().addListener(button1 -> registraFruitore());
        registrazioneView.mostra(gui);
    }

    private void registraFruitore() {
        String username = registrazioneView.getUsername();
        String password = registrazioneView.getPassword();
        String confermaPassword = registrazioneView.getConfermaPassword();
        try {
            loginService.registraFruitore(username, password, confermaPassword);
            registrazioneView.chiudi();
        } catch (Exception e) {
            registrazioneView.mostraErrore(e.getMessage());
        }
    }

    private void verificaCredenziali() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();
        try {
            utente = loginService.autentica(username, password);
            if (utente == null) {
                loginView.resetLogin();
                loginView.mostraErrore("Credenziali errate.");
            } else {
                loginView.chiudi();
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
            loginView.mostraErrore(e.getMessage());
        }
    }

    private void initListenerCambioPasswordView() {
        cambioPasswordView.getConfermaButton().addListener(button -> updatePassword());
    }

    private void updatePassword() {
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
