package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facade.ILuogoFacade;
import com.unibs.views.ElencoLuoghiView;
import com.unibs.views.RimuoviLuogoView;
import com.unibs.views.components.PopupChiudi;

public class LuogoController {
    private final ILuogoFacade luogoFacade;
    private final WindowBasedTextGUI gui;

    protected LuogoController(WindowBasedTextGUI gui, ILuogoFacade luogoFacade) {
        this.luogoFacade = luogoFacade;
        this.gui = gui;
    }

    public void apriVisualizzaElencoLuoghi()  {
        new ElencoLuoghiView().mostra(gui, luogoFacade.findAll());
    }

    public void apriRimuoviLuogo() {
        RimuoviLuogoView view = new RimuoviLuogoView();
        view.setLuoghi(luogoFacade.findAll());
        view.setOnLuogoSelected(idLuogo -> {
            try {
                luogoFacade.inserisciLuogoDaRimuovere(idLuogo);
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
