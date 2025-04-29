package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.services.*;
import com.unibs.views.MenuView;
import com.unibs.views.RegimeNonAttivoView;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ConfiguratoreController implements IUserController {

    // Generic
    private final MultiWindowTextGUI gui;
    private final Utente utente;

    // Controller
    private final LuogoController luogoController;
    private final TipoVisitaController tipoVisitaController;
    private final VisitaController visitaController;
    private final DatePrecluseController datePrecluseController;
    private final ConfigController configController;

    // Services
    private final ConfigService configService;

    // Views
    private final MenuView menuView;

    public ConfiguratoreController(MultiWindowTextGUI gui, Utente currentUtente, ServiceFactory serviceFactory) {
        this.gui = gui;
        this.utente = currentUtente;

        LuogoService luogoService = serviceFactory.getLuogoService();
        configService = serviceFactory.getConfigService();
        GiornoService giornoService = serviceFactory.getGiornoService();
        VolontarioService volontarioService = serviceFactory.getVolontarioService();
        VisitaService visitaService = serviceFactory.getVisitaService();
        DatePrecluseService datePrecluseService = serviceFactory.getDatePrecluseService();
        TipoVisitaService tipoVisitaService = serviceFactory.getTipoVisitaService();

        this.luogoController = new LuogoController(gui);
        this.tipoVisitaController = new TipoVisitaController(gui, luogoService, configService, giornoService, volontarioService, tipoVisitaService);
        this.visitaController = new VisitaController(gui, visitaService);
        this.datePrecluseController = new DatePrecluseController(gui, datePrecluseService);
        this.configController = new ConfigController(gui, configService);
        this.menuView = new MenuView(gui);
    }


    @Override
    public void start() {
        if (configService.isInitialized()) {
            if (configService.regimeAttivo()) {
                showMenu();
            } else {
                mostraAvvisoNonRegime(gui);
            }
        } else {
            inizializzaBaseDiDati();
        }
    }

    public void inizializzaBaseDiDati() {
        configService.initDefault();
        configController.apriConfigurazione();
        tipoVisitaController.apriAggiungiTipoVisita();
        configService.setInitializedOn(LocalDate.now());
    }

    @Override
    public void showMenu() {
        List<MenuOption> menuOptions = Arrays.asList(
                new MenuOption("Inserisci date precluse", (v) -> handleMenuAction(this::inserisciDatePrecluse)),
                new MenuOption("Modifica numero massimo persone", (v) -> handleMenuAction(this::modificaNumeroMaxPersone)),
                new MenuOption("Visualizza l’elenco dei volontari", (v) -> handleMenuAction(this::visualizzaElencoVolontari)),
                new MenuOption("Visualizza l’elenco dei luoghi visitabili", (v) -> handleMenuAction(this::visualizzaElencoLuoghi)),
                new MenuOption("Visualizza l’elenco dei luoghi con i relativi tipi di visita associati", (v) -> handleMenuAction(this::visualizzaLuoghiConTipiVisita)),
                new MenuOption("Visualizza l’elenco delle visite", (v) -> handleMenuAction(this::visualizzaVisite))
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

    private void visualizzaElencoVolontari() {
        tipoVisitaController.apriVisualizzaVisitePerVolontari();
    }

    public void mostraAvvisoNonRegime(WindowBasedTextGUI gui) {
        new RegimeNonAttivoView().mostra(gui);
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

    private void visualizzaLuoghiConTipiVisita() {
        tipoVisitaController.apriVisualizzaVisitePerLuoghi();
    }

    private void visualizzaVisite() {
        visitaController.apriVisualizzaVisitePerTipologia();
    }

}
