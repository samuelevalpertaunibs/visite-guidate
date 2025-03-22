package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.unibs.controllers.TipoVisitaController;
import com.unibs.controllers.VolontariController;
import com.unibs.models.Volontario;

import java.util.ArrayList;

public class VisualizzaElencoVolontari {
    private final VolontariController volontariController;
    private final TipoVisitaController tipoVisitaController;
    private final Label titleLabel;

    public VisualizzaElencoVolontari(VolontariController volontariController, TipoVisitaController tipoVisitaController) {
        this.volontariController = volontariController;
        this.tipoVisitaController = tipoVisitaController;
        this.titleLabel = new Label("");
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Visualizza Elenco Volontari");

        Panel panel = new Panel();
        ArrayList<String>  volontari = volontariController.getListaVolontari();
        for(String volontariNome : volontari) {
            int volontarioId = volontariController.getIdByUsername(volontariNome);
            ArrayList<String> titoloTipiVisita = (ArrayList<String>) tipoVisitaController.getTipiVisitaNomeByVolontarioId(volontarioId);

            panel.addComponent(new Label(volontariNome));

            if (titoloTipiVisita.isEmpty())
                panel.addComponent(new Label("Nessun tipo di visita associato"));
            else
                panel.addComponent(new Label(String.join(", ", titoloTipiVisita)));
        }

        Button closeButton = new Button("Chiudi", window::close);

        return window;
    }
}