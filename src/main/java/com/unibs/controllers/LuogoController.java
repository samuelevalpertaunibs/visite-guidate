package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.LuogoService;
import com.unibs.views.ElencoLuoghiView;

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

}
