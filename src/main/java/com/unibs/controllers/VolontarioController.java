package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.DatabaseException;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.services.*;
import com.unibs.views.InserisciDisponibilitaView;
import com.unibs.views.MenuView;
import com.unibs.views.RegimeNonAttivoView;

import java.time.LocalDate;
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
                new MenuOption("Visualizza tipi di visita", (v) -> handleMenuAction(this::visualizzaTipiVisiteAssociateAlVolontario)),
                new MenuOption("Inserisci disponibilità", (v) -> handleMenuAction(this::inserisciDisponibilita))
        );
        menuView.mostraMenu(menuOptions, " Menù principale - " + volontario.getUsername() + " ");
    }

    private void handleMenuAction(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            menuView.mostraErrore(e.getMessage());
        }
    }


    private void visualizzaTipiVisiteAssociateAlVolontario() {
        tipoVisitaController.apriVisualizzaTipiVisiteAssociateAlVolontario(volontario);
    }

    /*
    Il volontario puo inserire le disponibilità per il mese i+1 entro il 15/i.
    Il volontario puo sempre modificare le sue disponibilita in quanto il piano del mese i+1 verrà generato non prima del 16/i,
    di conseguenza nel giorno in cui verra generato il piano del mese i+1 il volontario potrà modificare solo le disponibilita del mese i+2, senza creare conflitti.
     */
    public void inserisciDisponibilita() {
        InserisciDisponibilitaView view = new InserisciDisponibilitaView();

        // Recupera date disponibili e le imposta nella view
        Set<LocalDate> dateDisponibiliSet = volontarioService.getDateDisponibiliPerVolontario(volontario);
        List<LocalDate> dateDisponibili = dateDisponibiliSet.stream()
                .sorted(Comparator.comparing(LocalDate::toString))
                .toList();
        view.setDateDisponibili(dateDisponibili);

        // Imposta azione del bottone Salva
        view.getSalvaButton().addListener((button) -> {
            List<LocalDate> selezionate = view.getDateSelezionate();
            sovrascriviDisponibilita(selezionate, view);
        });
        view.mostra(gui);
    }

    private void sovrascriviDisponibilita(List<LocalDate> selezionate, InserisciDisponibilitaView view) {
        try {
            volontarioService.sovrascriviDisponibilita(volontario, selezionate);
        } catch (DatabaseException e) {
            view.mostraErrore(e.getMessage());
        }
    }

}
