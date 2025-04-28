package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.services.ConfigService;
import com.unibs.views.MenuView;
import com.unibs.views.RegimeNonAttivoView;

import java.util.Arrays;
import java.util.List;


public class VolontarioController implements IUserController {
    private final Volontario utente;
    private final ConfigService configService;
    private final MenuView menuView;
    private final MultiWindowTextGUI gui;

    public VolontarioController(MultiWindowTextGUI gui, Utente currentUtente, ConfigService configService) {
        this.gui = gui;
        this.configService = configService;
        this.menuView = new MenuView(gui);
        this.utente = new Volontario(currentUtente);
    }

    @Override
    public void start() {
        if (configService.regimeAttivo()) {
            showMenu();
        }
        mostraAvvisoNonRegime(gui);
    }

    @Override
    public void mostraAvvisoNonRegime(WindowBasedTextGUI gui) {
        new RegimeNonAttivoView().mostra(gui);
    }

    @Override
    public void showMenu() {
        List<MenuOption> menuOptions = Arrays.asList(
                new MenuOption("Visualizza tipi di visita", (v) -> handleMenuAction(this::visualizzaVisite)),
                new MenuOption("Inserisci disponibilità", (v) -> handleMenuAction(this::inserisciDisponibilita))
        );
        menuView.mostraMenu(menuOptions, " Menù principale - " + utente.getUsername() + " ");
    }

    private void handleMenuAction(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            menuView.mostraErrore(e.getMessage());
        }
    }


    private void visualizzaVisite() {
        //tipoVisitaController.apriVisualizzaVisiteVolontario(utente);
    }

    /*
    Il volontario puo inserire le disponibilità per il mese i+1 entro il 15/i.
    Il volontario puo sempre modificare le sue disponibilita in quanto il piano del mese i+1 verrà generato non prima del 16/i,
    di conseguenza nel giorno in cui verra generato il piano del mese i+1 il volontario potrà modificare solo le disponibilita del mese i+2, senza creare conflitti.
     */
    private void inserisciDisponibilita() {
        //new InserisciDisponibilitaView(this).mostra(gui, utente.getId());
    }

    public List<String> getDatePerVolontario(int volontarioId) {
        return null;
    }

}
