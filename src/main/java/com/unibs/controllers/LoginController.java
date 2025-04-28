package com.unibs.controllers;

import com.unibs.services.*;
import com.unibs.models.Utente;
import com.googlecode.lanterna.gui2.*;
import com.unibs.views.CambioPasswordView;
import com.unibs.views.LoginView;

public class LoginController {

    private final LoginService loginService;
    private LoginView view;
    private final MultiWindowTextGUI gui;
    private CambioPasswordView cambioPasswordView;
    private Utente utente;

    public LoginController(MultiWindowTextGUI gui) {
        this.loginService = new LoginService();
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
        LuogoService luogoService = new  LuogoService ();
        ConfigService configService = new  ConfigService ();
        GiornoService giornoService = new  GiornoService ();
        UtenteService utenteService = new UtenteService();
        TipoVisitaService tipoVisitaService = new  TipoVisitaService();
        VisitaService visitaService = new  VisitaService ();
        DatePrecluseService datePrecluseService = new  DatePrecluseService();
        ConfiguratoreController configuratoreController = new ConfiguratoreController(gui, utente, luogoService, configService, giornoService, utenteService, tipoVisitaService, visitaService, datePrecluseService);
        configuratoreController.start();
    }

    private void inizializzaVolontario() {
        ConfigService configService = new  ConfigService();
        VolontarioController volontarioController = new VolontarioController(gui, utente, configService);
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
