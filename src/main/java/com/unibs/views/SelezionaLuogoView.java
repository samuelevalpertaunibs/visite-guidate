package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.unibs.controllers.LuogoController;
import com.unibs.models.Luogo;

public class SelezionaLuogoView {
    private final LuogoController luogoController;
    private Consumer<Luogo> onLuogoSelected;

    public SelezionaLuogoView(LuogoController luogoController) {
        this.luogoController = luogoController;
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Seleziona Luogo");

        Panel panel = new Panel();
        ArrayList<Luogo> luoghi = luogoController.getLuoghi();  // Ottieni la lista dei luoghi
        for (Luogo luogo: luoghi) {
            panel.addComponent(new Button(luogo.getNome(), () -> {
                if (onLuogoSelected != null) {
                    onLuogoSelected.accept(luogo);  // Passa il luogo selezionato al callback
                }
                window.close();
            }));
        }

        panel.addComponent(new Button("+ Aggiungi luogo", () -> {
            luogoController.creaInterfacciaAggiungiLuogo(nuovoLuogo -> {
                if (nuovoLuogo != null && onLuogoSelected != null) {
                    onLuogoSelected.accept(nuovoLuogo);
                }
            });

            window.close();
        }));

        window.setComponent(panel);
        return window;
    }

    public void setOnLuogoSelected(Consumer<Luogo> onLuogoSelected) {
        this.onLuogoSelected = onLuogoSelected;
    }
}
