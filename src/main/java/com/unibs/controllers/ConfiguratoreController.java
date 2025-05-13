package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.models.Visita;
import com.unibs.services.*;
import com.unibs.utils.DateService;
import com.unibs.views.MenuView;
import com.unibs.views.RegimeNonAttivoView;

import java.time.LocalDate;
import java.util.ArrayList;
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
    final List<MenuOption> menuOptions = new ArrayList<>();

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

        this.luogoController = new LuogoController(gui, luogoService);
        this.tipoVisitaController = new TipoVisitaController(gui, luogoService, configService, giornoService, volontarioService, tipoVisitaService);
        this.visitaController = new VisitaController(gui, visitaService);
        this.datePrecluseController = new DatePrecluseController(gui, datePrecluseService);
        this.configController = new ConfigController(gui, configService);
        this.menuView = new MenuView(gui);
    }


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

        // Imposto la data di inizio regime di funzionamento
        LocalDate oggi = DateService.today();
        LocalDate prossimoSedici = oggi.getDayOfMonth() < 16
                ? oggi.withDayOfMonth(16)
                : oggi.plusMonths(1).withDayOfMonth(16);
        configService.setPeriodoCorrente(prossimoSedici);
    }

    private void showMenu() {
        menuOptions.add(new MenuOption("Inserisci date precluse", (v) -> handleMenuAction(this::inserisciDatePrecluse)));
        menuOptions.add(new MenuOption("Modifica numero massimo persone", (v) -> handleMenuAction(this::modificaNumeroMaxPersone)));
        menuOptions.add(new MenuOption("Visualizza l’elenco dei volontari", (v) -> handleMenuAction(this::visualizzaElencoVolontari)));
        menuOptions.add(new MenuOption("Visualizza l’elenco dei luoghi visitabili", (v) -> handleMenuAction(this::visualizzaElencoLuoghi)));
        menuOptions.add(new MenuOption("Visualizza l’elenco dei luoghi con i relativi tipi di visita associati", (v) -> handleMenuAction(this::visualizzaLuoghiConTipiVisita)));
        menuOptions.add(new MenuOption("Visualizza l’elenco delle visite", (v) -> handleMenuAction(this::visualizzaVisite)));

        menuView.mostraMenu(menuOptions, " Menù principale - " + utente.getUsername() + " ", true);
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

    private void mostraAvvisoNonRegime(WindowBasedTextGUI gui) {
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
        visitaController.apriVisualizzaVisitePerTipologia(List.of(Visita.StatoVisita.values()));
    }

}
