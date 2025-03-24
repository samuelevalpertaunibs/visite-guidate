package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.views.MenuView;

import java.util.Arrays;
import java.util.List;


public class ConfiguratorController implements IUserController {
    private final Utente utente;
    private final InitController initController;
    private final DatePrecluseController datePrecluseController;
    private final ConfigController configController;
    private final VolontariController volontariController;
    private final TipoVisitaController tipoVisitaController;
    private final MenuView menuView;
    private final LuogoController luogoController;

    public ConfiguratorController(MultiWindowTextGUI gui, Utente currentUtente) {
        this.configController = new ConfigController(gui);
        this.luogoController = new LuogoController(gui);
        this.volontariController = new VolontariController(gui);
        this.tipoVisitaController = new TipoVisitaController(gui, luogoController, volontariController);
        this.initController = new InitController(configController, tipoVisitaController);
        this.datePrecluseController = new DatePrecluseController(gui);
        this.menuView = new MenuView(gui);
        this.utente = currentUtente;
    }

    @Override
    public void start() {
        initController.assertInizializzazione();
        showMenu();
    }

    @Override
    public void showMenu() {
        List<MenuOption> menuOptions = Arrays.asList(
                new MenuOption("Inserisci date precluse", (v) -> handleMenuAction(this::inserisciDatePrecluse)),
                new MenuOption("Modifica numero massimo persone", (v) -> handleMenuAction(this::modificaNumeroMaxPersone)),
                new MenuOption("Visualizza l’elenco dei volontari", (v) -> handleMenuAction(this::visualizzaElencoVolontari)),
                new MenuOption("Visualizza l’elenco dei luoghi visitabili", (v) -> handleMenuAction(this::visualizzaElencoLuoghi)),
                new MenuOption("Visualizza l’elenco dei luoghi con le relative visite associate", (v) -> handleMenuAction(this::visualizzaLuoghiConVisite))
        );
        menuView.mostraMenu(menuOptions);
    }

    private void handleMenuAction(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            menuView.mostraErrore(e.getMessage());
        }
    }

    private void visualizzaElencoVolontari() {
        tipoVisitaController.apriVisualizzaVisitePerVolontari();
    }

    private void modificaNumeroMaxPersone() {
        configController.apriModificaNumeroMax();
    }

    private void inserisciDatePrecluse() {
        datePrecluseController.apriAggiungiDatePrecluse();
    }

    private void visualizzaElencoLuoghi() {
        luogoController.apriVisualizzaElencoLuoghi();
    }

    private void visualizzaLuoghiConVisite() {
        tipoVisitaController.apriVisualizzaVisitePerLuoghi();
    }

}
