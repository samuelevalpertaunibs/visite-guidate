package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.LuogoService;
import com.unibs.views.ElencoLuoghiView;
import com.unibs.views.RimuoviLuogoView;
import com.unibs.views.components.PopupChiudi;

public class LuogoController {
    private final LuogoService luogoService;
    private final WindowBasedTextGUI gui;

    protected LuogoController(WindowBasedTextGUI gui, LuogoService luogoService) {
        this.luogoService = luogoService;
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
                luogoService.inserisciLuogoDaRimuovere(idLuogo);
                new PopupChiudi(gui).mostra("", "Il luogo verrà rimosso con successo");
            } catch (Exception e) {
                new PopupChiudi(gui).mostra("Errore", e.getMessage());
            } finally {
                view.chiudi();
            }
        });
        view.mostra(gui);
    }

}
