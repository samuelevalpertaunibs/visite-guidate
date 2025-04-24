package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.TipoVisitaController;
import com.unibs.controllers.VolontariController;
import com.unibs.models.Volontario;

import java.util.List;

public class ElencoVolontariView {
    private final VolontariController volontariController;
    private final TipoVisitaController tipoVisitaController;

    public ElencoVolontariView(TipoVisitaController tipoVisitaController) {
        this.tipoVisitaController = tipoVisitaController;
        this.volontariController = tipoVisitaController.getVolontariController();
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Visualizza Elenco Volontari");
        Panel panel = new Panel();

        List<Volontario> volontari = volontariController.getAllVolontari();

        for (Volontario volontario : volontari) {
            int volontarioId = volontario.getId();
            List<String> titoloTipiVisita = tipoVisitaController.getTitoliByVolontarioId(volontarioId);

            panel.addComponent(new Label("Volontario: " + volontario.getUsername()));

            String visitaText = titoloTipiVisita.isEmpty()
                    ? "Nessun tipo di visita associato"
                    : "Visite associate: " + String.join(", ", titoloTipiVisita);

            panel.addComponent(new Label(visitaText));
            panel.addComponent(new EmptySpace());
        }

        Button closeButton = new Button("Chiudi", window::close);
        panel.addComponent(closeButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);

        return window;
    }
}
