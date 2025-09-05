package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facades.*;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.models.Visita;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;
import com.unibs.views.ElencoVisiteView;
import com.unibs.views.InserisciDisponibilitaView;
import com.unibs.views.MenuView;
import com.unibs.views.RegimeNonAttivoView;
import com.unibs.views.components.PopupChiudi;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;


public class VolontarioController implements IUserController {
    private final Volontario volontario;
    private final MenuView menuView;
    private final MultiWindowTextGUI gui;
    private final TipoVisitaController tipoVisitaController;
    private ElencoVisiteView elencoVisiteView;
    private final IVolontarioFacade volontarioFacade;

    public VolontarioController(MultiWindowTextGUI gui, Utente currentUtente, IVolontarioFacade volontarioFacade, ITipoVisitaFacade tipoVisitaFacade) {
        this.gui = gui;
        this.menuView = new MenuView(gui);
        this.volontario = new Volontario(currentUtente);
        this.tipoVisitaController = new TipoVisitaController(gui, tipoVisitaFacade);
        this.volontarioFacade = volontarioFacade;
    }

    public void start() {
        if (volontarioFacade.regimeAttivo()) {
            showMenu();
        } else {
            mostraAvvisoNonRegime(gui);
        }
    }

    public void mostraAvvisoNonRegime(WindowBasedTextGUI gui) {
        new RegimeNonAttivoView().mostra(gui);
    }

    public void showMenu() {
        List<MenuOption> menuOptions = Arrays.asList(
                new MenuOption("Inserisci disponibilità", (v) -> handleMenuAction(this::apriInserisciDisponibilita)),
                new MenuOption("Visualizza tipi di visita", (v) -> handleMenuAction(this::apriVisualizzaTipiVisiteAssociate)),
                new MenuOption("Visualizza le tue visite", (v) -> handleMenuAction(this::apriVisualizzaVisite))
        );
        menuView.mostraMenu(menuOptions, " Menù principale - " + volontario.getUsername() + " ", true);
    }

    public void apriVisualizzaVisite() {
        elencoVisiteView = new ElencoVisiteView("Visualizza elenco visite a cui sei associato", true);
        List<Visita.StatoVisita> statiDaMostrare = List.of(Visita.StatoVisita.PROPOSTA, Visita.StatoVisita.CONFERMATA, Visita.StatoVisita.COMPLETA, Visita.StatoVisita.CANCELLATA);
        initElencoVisiteConIscrizioneViewListener(statiDaMostrare);
        elencoVisiteView.mostra(gui);
    }

    private void initElencoVisiteConIscrizioneViewListener(List<Visita.StatoVisita> stati) {
        try {
            elencoVisiteView.setStati(stati, stato -> {
                List<Visita> visite = volontarioFacade.getVisitePreviewByVolontario(stato, volontario);
                List<List<String>> codiciPrenotazione = new ArrayList<>();
                for (Visita visita : visite) {
                    codiciPrenotazione.add(volontarioFacade.getCodiciPrenotazionePerVisita(volontario, visita.getId()));
                }
                elencoVisiteView.aggiornaVisite(visite, stato, codiciPrenotazione);
            });
        } catch (Exception e) {
            elencoVisiteView.mostraErrore(e.getMessage());
        }
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
            if (volontarioFacade.raccoltaDisponibilitaChiusa()) {
                new PopupChiudi(gui).mostra("Attenzione", "La raccolta delle disponibilità dei volontari è momentaneamente chiusa.");
                return;
            }

            YearMonth mese = volontarioFacade.getMesePeriodoCorrente().plusMonths(2);

            // Recupero date disponibili e le imposto nella view
            Set<LocalDate> dateSet = volontarioFacade.calcolaDateDiCuiRichiedereDisponibilita(volontario, mese);
            List<LocalDate> dateDisponibili = dateSet.stream()
                    .sorted(Comparator.comparing(LocalDate::toString))
                    .toList();
            view.setDateDisponibili(dateDisponibili);

            List<LocalDate> dateAttualmenteDisponibili = volontarioFacade.getDateDisponibiliByMese(volontario.getId(), mese);
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
            volontarioFacade.sovrascriviDisponibilita(volontario, selezionate);
            new PopupChiudi(gui).mostra("", "Aggiornamento avvenuto con successo.");
        } catch (DatabaseException e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
    }

}
