package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.VisitaService;
import com.unibs.views.ElencoVisiteView;
import com.unibs.models.Visita;

import java.util.List;

public class VisitaController {
    private final WindowBasedTextGUI gui;
    private final VisitaService visitaService;
    private ElencoVisiteView elencoVisiteView;

    public VisitaController(WindowBasedTextGUI gui, VisitaService visitaService) {
        this.gui = gui;
        this.visitaService = visitaService;
    }

    public void apriVisualizzaVisitePerTipologia() {
        elencoVisiteView = new ElencoVisiteView();
        initElencoVisiteViewListener();
        elencoVisiteView.mostra(gui);
    }

    private void initElencoVisiteViewListener() {
        try {
            elencoVisiteView.setStati(List.of(Visita.StatoVisita.values()), stato -> {
                List<Visita> visite = visitaService.getVisitePreview(stato);
                elencoVisiteView.aggiornaVisiteTable(visite, stato);
            });
        } catch (Exception e) {
            elencoVisiteView.mostraErrore(e.getMessage());
        }
    }
}
