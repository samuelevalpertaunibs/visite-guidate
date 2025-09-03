package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facade.FruitoreFacade;
import com.unibs.models.Fruitore;
import com.unibs.models.MenuOption;
import com.unibs.models.Utente;
import com.unibs.models.Visita;
import com.unibs.services.ServiceFactory;
import com.unibs.views.InputNumericoView;
import com.unibs.views.MenuView;
import com.unibs.views.RegimeNonAttivoView;
import com.unibs.views.SelezionaElementoView;
import com.unibs.views.components.PopupChiudi;

import java.util.Arrays;
import java.util.List;

public class FruitoreController implements IUserController {
    private final Fruitore fruitore;
    private final WindowBasedTextGUI gui;
    private final MenuView menuView;
    private final VisitaController visitaController;
    private final FruitoreFacade fruitoreFacade;

    public FruitoreController(MultiWindowTextGUI gui, Utente utente, ServiceFactory serviceFactory) {
        this.gui = gui;
        this.menuView = new MenuView(gui);
        this.fruitore = new Fruitore(utente);
        this.visitaController = new VisitaController(gui, serviceFactory);
        this.fruitoreFacade = new FruitoreFacade(serviceFactory);
    }

    public void start() {
        if (fruitoreFacade.regimeAttivo()) {
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
                new MenuOption("Visualizza tutte le visite", (v) -> handleMenuAction(this::apriVisualizzaTutteVisite)),
                new MenuOption("Iscriviti ad una visita proposta", (v) -> handleMenuAction(this::apriIscrivitiVisitaProposta)),
                new MenuOption("Visualizzare le visite a cui sei iscritto", (v) -> handleMenuAction(this::apriVisiteConIscrizione)),
                new MenuOption("Disdici un'iscrizione", (v) -> handleMenuAction(this::apriDisdiciIscrizione))

        );
        menuView.mostraMenu(menuOptions, " Menù principale - " + fruitore.getUsername() + " ", true);
    }

    private void apriDisdiciIscrizione() {
        try {
            InputNumericoView inputNumericoView = new InputNumericoView("Disdici iscrizione", "Inserisci il codice univoco relativo all'iscrizione da disdire");
            Integer codiceIscrizione = inputNumericoView.mostra(gui);
            if (codiceIscrizione != null) {
                fruitoreFacade.disdici(fruitore, codiceIscrizione);
                new PopupChiudi(gui).mostra("", "L'iscrizione è stata annullata correttamente.");
            }
        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
    }

    private void apriIscrivitiVisitaProposta() {
        SelezionaElementoView<Visita> selezionaVisitaView = new SelezionaElementoView<>();
        Visita visitaSelezionata = null;
        try {
            List<Visita> visite = fruitoreFacade.getVisitePreviewByStato(Visita.StatoVisita.PROPOSTA);
            InputNumericoView inputNumericoView = new InputNumericoView("Iscrizione", "Inserisci il numero di persone per cui vuoi prenotare");

            if (visite.isEmpty()) {
                throw new Exception("Nessuna visita attualmente dispobile");
            }

            visitaSelezionata = selezionaVisitaView.mostra(gui, visite, "Seleziona una visita");
            Integer numeroIscritti = inputNumericoView.mostra(gui);
            if (numeroIscritti != null) {
                int codiceUnivoco = fruitoreFacade.iscrivi(fruitore, visitaSelezionata, numeroIscritti);
                new PopupChiudi(gui).mostra("", "L'iscrizione è avvenuta con successo.\nQuesto è il tuo codice prenotazione: " + codiceUnivoco + ".\nPortalo il giorno della visita o utilizzalo per annullare l'iscrizione.");
            }
        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        } finally {
           if(visitaSelezionata != null) {
               selezionaVisitaView.chiudi();
           }
        }
    }

    private void apriVisualizzaTutteVisite() {
        visitaController.apriVisualizzaVisitePerTipologia(List.of(Visita.StatoVisita.PROPOSTA, Visita.StatoVisita.CONFERMATA, Visita.StatoVisita.COMPLETA, Visita.StatoVisita.CANCELLATA));
    }

    private void apriVisiteConIscrizione() {
        visitaController.apriVisualizzaVisiteConIscrizione(fruitore.getUsername(), List.of(Visita.StatoVisita.PROPOSTA, Visita.StatoVisita.CONFERMATA, Visita.StatoVisita.COMPLETA, Visita.StatoVisita.CANCELLATA));
    }

    private void handleMenuAction(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            menuView.mostraErrore(e.getMessage());
        }
    }
}
