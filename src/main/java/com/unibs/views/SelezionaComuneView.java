package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.util.ArrayList;

import com.unibs.controllers.ConfigController;
import com.unibs.models.Comune;

public class SelezionaComuneView {
    private final ConfigController configController;
    private Comune comuneSelezionato = null;
    //private

    public SelezionaComuneView(ConfigController configController) {
        this.configController = configController;
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Seleziona un Comune");

        Panel panel = new Panel();

        ArrayList<Comune> comuni = configController.getAmbitoTerritoriale();

        for (Comune comune : comuni) {
            panel.addComponent(new Button(comune.getNome(), () -> {
                comuneSelezionato = comune;
                window.close();
            }));
        }

        window.setComponent(panel);
        return window;
    }

    public Comune getComuneSelezionato() {
        return this.comuneSelezionato;
    }
}
