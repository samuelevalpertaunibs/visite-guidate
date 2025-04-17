package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.TipoVisitaService;
import com.unibs.views.AggiungiTipoVisitaView;
import com.unibs.views.ElencoLuoghiConVisiteAssociate;
import com.unibs.views.ElencoVolontariView;

import java.util.List;

public class TipoVisitaController {
    private final TipoVisitaService tipoVisitaService;
    private final AggiungiTipoVisitaView aggiungiTipoVisitaView;
    private final ElencoVolontariView elencoVolontariView;
    private final WindowBasedTextGUI gui;
    private final LuogoController luogoController;
    private final VolontariController volontariController;
    private final ElencoLuoghiConVisiteAssociate elencoLuoghiConVisiteAssociate;

    protected TipoVisitaController(WindowBasedTextGUI gui, LuogoController luogoController, VolontariController volontariController) {
        this.gui = gui;
        this.tipoVisitaService = new TipoVisitaService();
        this.luogoController = luogoController;
        this.volontariController = volontariController;
        this.aggiungiTipoVisitaView = new AggiungiTipoVisitaView(this);
        this.elencoVolontariView = new ElencoVolontariView(this);
        this.elencoLuoghiConVisiteAssociate = new ElencoLuoghiConVisiteAssociate(this);
    }

    public void apriAggiungiTipoVisita() {
        gui.addWindowAndWait(aggiungiTipoVisitaView.creaFinestra());
    }

    public void aggiungiTipoVisita(String titolo, String descrizione, String dataInizio,
            String dataFine, String oraInizio, String durata, String entrataLibera,
            String numeroMinPartecipanti, String numeroMaxPartecipanti, String nomeLuogoSelezionato,
            String[] volontari, String[] giorni, String indirizzoPuntoIncontro) {

        // Controllo che il luogo sia stato selezionato, non è compito del service
        // perchè lui si aspetta un nomeLuogo
        if (nomeLuogoSelezionato.equalsIgnoreCase("Nessun luogo selezionato")) {
            aggiungiTipoVisitaView.mostraErrore("Seleziona un luogo prima di preseguire");
            return;
        }
        try {
            tipoVisitaService.aggiungiTipoVisita(titolo, descrizione, dataInizio,
                    dataFine, oraInizio, durata, entrataLibera,
                    numeroMinPartecipanti, numeroMaxPartecipanti, nomeLuogoSelezionato, volontari, giorni, indirizzoPuntoIncontro);
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
        gui.removeWindow(gui.getActiveWindow());
    }

    public List<String> getTitoliByVolontarioId(int volontarioId) {
        return tipoVisitaService.getTitoliByVolontarioId(volontarioId);
    }

    public List<String> getTitoliByLuogoId(int luogoId) {
        return tipoVisitaService.getTitoliByLuogoId(luogoId);
    }

    public void apriVisualizzaVisitePerVolontari() {
        gui.addWindowAndWait(elencoVolontariView.creaFinestra());
    }

    public LuogoController getLuogoController() {
        return luogoController;
    }

    public VolontariController getVolontariController() {
        return volontariController;
    }

    public void apriVisualizzaVisitePerLuoghi() {
        gui.addWindowAndWait(elencoLuoghiConVisiteAssociate.creaFinestra());
    }
}
