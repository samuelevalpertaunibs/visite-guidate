package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.Visita;
import com.unibs.services.VisitaService;
import com.unibs.views.ElencoVisiteView;

import java.util.List;

public class VisitaController {
    private final WindowBasedTextGUI gui;
    private final VisitaService visitaService;
    private ElencoVisiteView elencoVisiteView;

    public VisitaController(WindowBasedTextGUI gui, VisitaService visitaService) {
        this.gui = gui;
        this.visitaService = visitaService;
    }

    public void apriVisualizzaVisitePerTipologia(List<Visita.StatoVisita> statiDaMostrare) {
        elencoVisiteView = new ElencoVisiteView("Visualizza elenco visite per stato");
        initElencoVisiteViewListener(statiDaMostrare);
        elencoVisiteView.mostra(gui);
    }

    private void initElencoVisiteViewListener(List<Visita.StatoVisita> stati) {
        try {
            elencoVisiteView.setStati(stati, stato -> {
                List<Visita> visite;
                if (stato != Visita.StatoVisita.EFFETTUATA) {
                    visite = visitaService.getVisitePreviewByStato(stato);
                } else {
                    visite = visitaService.getVisiteFromArchivio();
                }
                elencoVisiteView.aggiornaVisite(visite, stato);
            });
        } catch (Exception e) {
            elencoVisiteView.mostraErrore(e.getMessage());
        }
    }

}
