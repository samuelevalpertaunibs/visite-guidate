package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.LuogoService;
import com.unibs.services.VolontarioService;
import com.unibs.views.ElencoLuoghiView;
import com.unibs.views.RimuoviLuogoView;
import com.unibs.views.components.PopupChiudi;

public class LuogoController {
    private final LuogoService luogoService;
    private final VolontarioService volontarioService;
    private final WindowBasedTextGUI gui;

    protected LuogoController(WindowBasedTextGUI gui, LuogoService luogoService, VolontarioService volontarioService) {
        this.luogoService = luogoService;
        this.volontarioService = volontarioService;
        this.gui = gui;
    }

    public void apriVisualizzaElencoLuoghi() {
        new ElencoLuoghiView().mostra(gui, luogoService.findAll());
    }

    public void apriRimuoviLuogo() {
        RimuoviLuogoView view = new RimuoviLuogoView();
        view.setLuoghi(luogoService.findAll());
        view.setOnLuogoSelected(idLuogo -> {
            try {
                luogoService.rimuoviById(idLuogo);
                volontarioService.rimuoviNonAssociati();
                new PopupChiudi(gui).mostra("", "Il luogo è stato rimosso con successo");
            } catch (Exception e) {
                new PopupChiudi(gui).mostra("Errore", e.getMessage());
            } finally {
                view.chiudi();
            }
        });
        view.mostra(gui);
    }
}
