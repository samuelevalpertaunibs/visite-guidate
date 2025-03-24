package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.LuogoController;
import com.unibs.models.Luogo;

import java.util.List;

public class ElencoLuoghiView {
    private final LuogoController luogoController;

    public ElencoLuoghiView(LuogoController luogoController) {
        this.luogoController = luogoController;
    }

    public Window creaFinestra() {

        Window window = new BasicWindow("Visualizza elenco luoghi visitabili");

        Panel panel = new Panel();

        List<Luogo> luoghi = luogoController.getLuoghi();

        for (Luogo luogo : luoghi) {
            panel.addComponent(new Label("- " + luogo.getNome()));
            panel.addComponent(new EmptySpace());
        }

        Button closeButton = new Button("Chiudi", window::close);
        panel.addComponent(closeButton);

        window.setComponent(panel);

        return window;
    }
}
