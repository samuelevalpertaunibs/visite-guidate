package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facades.*;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.models.Visita;
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
    MenuOption creaPianoOption;
    MenuView operazioniSupplementariMenu;
    private final IConfiguratoreFacade configuratoreFacade;

    // Views
    private final MenuView menuView;

    public ConfiguratoreController(MultiWindowTextGUI gui, Utente currentUtente, IVisitaFacade visitaFacade, IConfiguratoreFacade configuratoreFacade, ITipoVisitaFacade tipoVisitaFacade, IConfigFacade configFacade, IDatePrecluseFacade datePrecluseFacade, ILuogoFacade luogoFacade) {
        this.gui = gui;
        this.utente = currentUtente;
        this.luogoController = new LuogoController(gui, luogoFacade);
        this.tipoVisitaController = new TipoVisitaController(gui, tipoVisitaFacade);
        this.visitaController = new VisitaController(gui,visitaFacade);
        this.datePrecluseController = new DatePrecluseController(gui, datePrecluseFacade);
        this.configController = new ConfigController(gui, configFacade);
        this.menuView = new MenuView(gui);
        this.configuratoreFacade = configuratoreFacade;
    }


    public void start() {
        if (configuratoreFacade.isInitialized()) {
            if (configuratoreFacade.regimeAttivo()) {
                showMenu();
            } else {
                mostraAvvisoNonRegime(gui);
            }
        } else {
            inizializzaBaseDiDati();
        }
    }

    public void inizializzaBaseDiDati() {
        configuratoreFacade.inizializzaConfigurazioneDefault();
        configController.apriConfigurazione();
        tipoVisitaController.apriAggiungiTipoVisita();

        // Imposto la data di inizio regime di funzionamento
        LocalDate oggi = DateService.today();
        LocalDate prossimoSedici = oggi.getDayOfMonth() < 16
                ? oggi.withDayOfMonth(16)
                : oggi.plusMonths(1).withDayOfMonth(16);
        configuratoreFacade.setPeriodoCorrente(prossimoSedici);
    }

    public void showMenu() {
        menuOptions.add(new MenuOption("Inserisci date precluse", (v) -> handleMenuAction(this::inserisciDatePrecluse)));
        menuOptions.add(new MenuOption("Modifica numero massimo persone", (v) -> handleMenuAction(this::modificaNumeroMaxPersone)));
        menuOptions.add(new MenuOption("Visualizza l’elenco dei volontari", (v) -> handleMenuAction(this::visualizzaElencoVolontari)));
        menuOptions.add(new MenuOption("Visualizza l’elenco dei luoghi visitabili", (v) -> handleMenuAction(this::visualizzaElencoLuoghi)));
        menuOptions.add(new MenuOption("Visualizza l’elenco dei luoghi con i relativi tipi di visita associati", (v) -> handleMenuAction(this::visualizzaLuoghiConTipiVisita)));
        menuOptions.add(new MenuOption("Visualizza l’elenco delle visite", (v) -> handleMenuAction(this::visualizzaVisite)));

        if (configuratoreFacade.isCreazioneNuovoPianoPossibile()) {
            creaPianoOption = new MenuOption("Creazione nuovo piano", (v) -> handleMenuAction(this::incrementaPeriodoCorrente));
            menuOptions.add(creaPianoOption);
        }

        menuView.mostraMenu(menuOptions, " Menù principale - " + utente.getUsername() + " ", true);
    }

    private void incrementaPeriodoCorrente() {
        configuratoreFacade.applicaRimozioni();

        Boolean isPianoCreato = visitaController.apriCreazionePiano();

        // Se il piano era gia stato creato
        if (isPianoCreato == null) {
            menuOptions.remove(creaPianoOption);
            menuView.aggiornaMenu(menuOptions, " Menù principale - " + utente.getUsername() + " ", true);
        } else {
            // Il piano è appena stato creato
            if (isPianoCreato) {
                List<MenuOption> subMenuOptions = new ArrayList<>();
                subMenuOptions.add(new MenuOption("Inserisci un nuovo luogo", (v) -> handleMenuAction(this::inserisciNuovoLuogo) ));
                subMenuOptions.add(new MenuOption("Inserisci un nuovo tipo di visita", (v) -> handleMenuAction(this::inserisciNuovoTipoVisita) ));
                subMenuOptions.add(new MenuOption("Associa dei volontari a tipi di visita gia esistenti", (v) -> handleMenuAction(this::associaAltriVolontari) ));
                subMenuOptions.add(new MenuOption("Rimuovi un luogo", (v) -> handleMenuAction(this::rimuoviLuogo) ));
                subMenuOptions.add(new MenuOption("Rimuovi un tipo di visita", (v) -> handleMenuAction(this::rimuoviTipoVisita) ));
                subMenuOptions.add(new MenuOption("Rimuovi un volontario dall’elenco dei volontari", (v) -> handleMenuAction(this::rimuoviVolontario)));
                subMenuOptions.add(new MenuOption("Riapri la raccolta delle disponibilità dei volontari", (v) -> handleMenuAction(() -> {
                    configuratoreFacade.riapriRaccoltaDisponibilita();
                    operazioniSupplementariMenu.close();
                    menuOptions.remove(creaPianoOption);
                    menuView.aggiornaMenu(menuOptions, " Menù principale - " + utente.getUsername() + " ", true);
                })));

                operazioniSupplementariMenu = new MenuView(gui);
                operazioniSupplementariMenu.mostraMenu(subMenuOptions, "Operazioni supplementari", false);
            }
        }
    }


    private void associaAltriVolontari() {
        tipoVisitaController.associaNuoviVolontari();
    }

    private void inserisciNuovoTipoVisita() {
        tipoVisitaController.apriInserisciNuovoTipoVisita();
    }

    private void rimuoviVolontario() {
        tipoVisitaController.apriRimuoviVolontario();
    }

    private void rimuoviTipoVisita() {
        tipoVisitaController.apriRimuoviTipoVisita();
    }

    private void rimuoviLuogo() {
        luogoController.apriRimuoviLuogo();
    }

    private void inserisciNuovoLuogo() {
        tipoVisitaController.apriInserisciNuovoLuogo();
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
        visitaController.apriVisualizzaVisitePerTipologia(List.of(Visita.StatoVisita.values()));
    }

}
