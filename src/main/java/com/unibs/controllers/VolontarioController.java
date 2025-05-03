package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.services.*;
import com.unibs.utils.DatabaseException;
import com.unibs.views.InserisciDisponibilitaView;
import com.unibs.views.MenuView;
import com.unibs.views.RegimeNonAttivoView;
import com.unibs.views.components.PopupChiudi;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


public class VolontarioController implements IUserController {
    private final Volontario volontario;
    private final ConfigService configService;
    private final MenuView menuView;
    private final MultiWindowTextGUI gui;
    private final TipoVisitaController tipoVisitaController;
    private final VolontarioService volontarioService;
    private final TipoVisitaService tipoVisitaService;

    public VolontarioController(MultiWindowTextGUI gui, Utente currentUtente, ServiceFactory serviceFactory) {
        this.gui = gui;
        this.configService = serviceFactory.getConfigService();
        this.volontarioService = serviceFactory.getVolontarioService();
        this.menuView = new MenuView(gui);
        this.volontario = new Volontario(currentUtente);

        LuogoService luogoService = serviceFactory.getLuogoService();
        ConfigService configService = serviceFactory.getConfigService();
        GiornoService giornoService = serviceFactory.getGiornoService();
        tipoVisitaService = serviceFactory.getTipoVisitaService();

        this.tipoVisitaController = new TipoVisitaController(gui, luogoService, configService, giornoService, volontarioService, tipoVisitaService);
    }

    @Override
    public void start() {
        if (configService.regimeAttivo()) {
            showMenu();
        } else {
            mostraAvvisoNonRegime(gui);
        }
    }

    @Override
    public void mostraAvvisoNonRegime(WindowBasedTextGUI gui) {
        new RegimeNonAttivoView().mostra(gui);
    }

    @Override
    public void showMenu() {
        List<MenuOption> menuOptions = Arrays.asList(
                new MenuOption("Inserisci disponibilità", (v) -> handleMenuAction(this::apriInserisciDisponibilita)),
                new MenuOption("Visualizza tipi di visita", (v) -> handleMenuAction(this::apriVisualizzaTipiVisiteAssociate))
        );
        menuView.mostraMenu(menuOptions, " Menù principale - " + volontario.getUsername() + " ", true);
    }

    private void handleMenuAction(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            menuView.mostraErrore(e.getMessage());
        }
    }


    private void apriVisualizzaTipiVisiteAssociate() {
        tipoVisitaController.apriVisualizzaTipiVisiteAssociateAlVolontario(volontario);
    }

    public void apriInserisciDisponibilita() {
        InserisciDisponibilitaView view = new InserisciDisponibilitaView();
        try {

            // Se il configuratore sta creando il piano visite in questo momento la raccolta disponbilita è chiusa
            if (configService.isRaccoltaDisponibilitaChiusa()) {
                new PopupChiudi(gui).mostra("Attenzione", "La raccolta delle disponibilità dei volontari è momentaneamente chiusa.");
                return;
            }

            YearMonth mese = configService.getMesePeriodoCorrente().plusMonths(2);

            // Recupero date disponibili e le imposto nella view
            Set<LocalDate> dateSet = volontarioService.calcolaDateDiCuiRichiedereDisponibilitaPerVolontario(volontario, mese);
            List<LocalDate> dateDisponibili = dateSet.stream()
                    .sorted(Comparator.comparing(LocalDate::toString))
                    .toList();
            view.setDateDisponibili(dateDisponibili);

            List<LocalDate> dateAttualmenteDisponibili = volontarioService.getDateDisponibiliByMese(volontario, mese);
            view.setDateSelezionate(dateAttualmenteDisponibili);

            // Imposto azione del bottone Salva
            view.getSalvaButton().addListener((button) -> {
                List<LocalDate> selezionate = view.getDateSelezionate();
                sovrascriviDisponibilita(selezionate);
            });
            view.mostra(gui);
        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
    }

    private void sovrascriviDisponibilita(List<LocalDate> selezionate) {
        try {
            volontarioService.sovrascriviDisponibilita(volontario, selezionate);
            new PopupChiudi(gui).mostra("", "Aggiornamento avvenuto con successo.");
        } catch (DatabaseException e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
    }

}
