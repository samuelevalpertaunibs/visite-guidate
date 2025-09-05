package com.unibs.controllers;

import com.unibs.facade.*;
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
        } else if (currentUtente.getRole() == 3) {
            inizializzaFruitore();
        } else {
            loginView.mostraErrore("Ruolo non riconosciuto.");
        }
    }

    private void inizializzaFruitore() {
        FruitoreController fruitoreController = new FruitoreController(gui, utente, new VisitaFacade(serviceFactory), new FruitoreFacade(serviceFactory));
        fruitoreController.start();
    }

    private void inizializzaConfiguratore() {
        ConfiguratoreController configuratoreController = new ConfiguratoreController(gui, utente, new VisitaFacade(serviceFactory), new ConfiguratoreFacade(serviceFactory), new TipoVisiteFacade(serviceFactory), new ConfigFacade(serviceFactory), new DatePrecluseFacade(serviceFactory), new LuogoFacade(serviceFactory));
        configuratoreController.start();
    }

    private void inizializzaVolontario() {
        VolontarioController volontarioController = new VolontarioController(gui, utente,new VolontarioFacade(serviceFactory), new TipoVisiteFacade(serviceFactory));
        volontarioController.start();
    }

    public void apriLogin(MultiWindowTextGUI gui) {
        loginView = new LoginView();
        loginView.getLoginButton().addListener(this::verificaCredenziali);
        loginView.getRegistratiButton().addListener(this::apriRegistrazione);
        loginView.mostra(gui);
    }

    private void apriRegistrazione(Button button) {
        registrazioneView = new RegistrazioneView();
        registrazioneView.getRegistratiButton().addListener(this::registraFruitore);
        registrazioneView.mostra(gui);
    }

    private void registraFruitore(Button button) {
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

    private void verificaCredenziali(Button button) {
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
