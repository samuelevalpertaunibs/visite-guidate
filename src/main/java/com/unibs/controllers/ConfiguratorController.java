package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.unibs.models.MenuOption;
import com.unibs.models.User;
import com.unibs.views.MenuView;
import java.util.Arrays;
import java.util.List;


public class ConfiguratorController implements IUserController {
    private final User user;
    private final InitController initController;
    private final DatePrecluseController datePrecluseController;
    private final ConfigController configController;
    private final MenuView menuView;

    public ConfiguratorController(MultiWindowTextGUI gui, User currentUser) {
        this.configController = new ConfigController(gui);
        this.initController = new InitController(gui, configController);
        this.datePrecluseController = new DatePrecluseController(gui);
        this.menuView = new MenuView(gui);
        this.user = currentUser;
    }

    @Override
    public void start() {
        // Se le configurazioni non sono ancora state inizializzate
        assertInizializzazione();
        showMenu();
    }

    private void assertInizializzazione() {
        initController.assertInizializzazione();
    }

    @Override
    public void showMenu() {

        List<MenuOption> menuOptions = Arrays.asList(
                new MenuOption("Inserisci date precluse", (v) -> inserisciDatePrecluse()),
                new MenuOption("Modifica numero massimo persone", (v) -> modificaNumeroMaxPersone())
        );

        menuView.mostraMenu(menuOptions);
    }

    private void modificaNumeroMaxPersone() {
        try {
            configController.apriModificaNumeroMax();
        } catch (Exception e) {
            menuView.mostraErrore(e.getMessage());
        }
    }

    private void inserisciDatePrecluse() {
        try {
            datePrecluseController.apriAggiungiDatePrecluse();
        } catch (Exception e) {
            menuView.mostraErrore(e.getMessage());
        }
    }

}
