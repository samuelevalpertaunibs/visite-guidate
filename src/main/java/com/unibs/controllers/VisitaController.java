package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.VisitaService;
import com.unibs.views.ElencoVisiteView;
import com.unibs.models.Visita;

import java.util.List;

public class VisitaController {
    private final WindowBasedTextGUI tui;
    private final VisitaService visitaService;
    private final ElencoVisiteView elencoVisiteView;

    public VisitaController(WindowBasedTextGUI tui) {
        this.tui = tui;
        this.visitaService = new VisitaService();
        this.elencoVisiteView = new ElencoVisiteView(this);
    }

    public void apriVisualizzaVisitePerTipologia() {
        tui.addWindowAndWait(elencoVisiteView.creaFinestra());
    }

    public List<Visita> getVisitePreview(Visita.StatoVisita stato) {
        return visitaService.getVisitePreview(stato);
    }
}
