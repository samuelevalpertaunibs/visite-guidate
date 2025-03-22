package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.TipoVisitaService;
import com.unibs.views.AggiungiTipoVisitaView;
import java.util.List;

public class TipoVisitaController {
    private final TipoVisitaService tipoVisitaService;
    private final AggiungiTipoVisitaView aggiungiTipoVisitaView;
    private final WindowBasedTextGUI gui;
    private final ConfigController configController;

    protected TipoVisitaController(WindowBasedTextGUI gui, ConfigController configController) {
        this.tipoVisitaService = new TipoVisitaService();
        this.gui = gui;
        this.configController = configController;
        this.aggiungiTipoVisitaView = new AggiungiTipoVisitaView(this, new LuogoController(gui));
    }

    public void apriAggiungiTipoVisita() {
        gui.addWindowAndWait(aggiungiTipoVisitaView.creaFinestra());
    }

    public void aggiungiTipoVisita(String titolo, String descrizione, String dataInizio,
            String dataFine, String oraInizio, String durata, String entrataLibera,
            String numeroMinPartecipanti, String numeroMaxPartecipanti, String nomeLuogoSelezionato,
            String[] volontari, String[] giorni) {

        // Controllo che il luogo sia stato selezionato, non è compito del service
        // perchè lui si aspetta un nomeLuogo
        if (nomeLuogoSelezionato.equalsIgnoreCase("Nessun luogo selezionato")) {
            aggiungiTipoVisitaView.mostraErrore("Seleziona un luogo prima di preseguire");
            return;
        }
        try {
            tipoVisitaService.aggiungiTipoVisita(titolo, descrizione, dataInizio,
                    dataFine, oraInizio, durata, entrataLibera,
                    numeroMinPartecipanti, numeroMaxPartecipanti, nomeLuogoSelezionato, volontari, giorni);
            aggiungiTipoVisitaView.clearAll();
        } catch (Exception e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    public WindowBasedTextGUI getGui() {
        return this.gui;
    }

    public List<String> getGiorniSettimana() {
        return tipoVisitaService.getGiorniSettimana();
    }

    public void chiudiFinestraAggiungiTipoVisita() {
        try {
            if (tipoVisitaService.isEmpty()) {
                aggiungiTipoVisitaView.mostraErrore("Inserisci almeno un tipo di visita.");
                return;
            }
        } catch (Exception e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
        // Chiudo AggiungiTipoVisitaView
        configController.setIsInitialized(true);
        gui.removeWindow(gui.getActiveWindow());
    }
}
