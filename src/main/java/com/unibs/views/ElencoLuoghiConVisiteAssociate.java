package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.LuogoController;
import com.unibs.controllers.TipoVisitaController;
import com.unibs.models.Luogo;

import java.util.List;

public class ElencoLuoghiConVisiteAssociate {
    private final LuogoController luogoController;
    private final TipoVisitaController tipoVisitaController;

    public ElencoLuoghiConVisiteAssociate(TipoVisitaController tipoVisitaController) {
        this.tipoVisitaController = tipoVisitaController;
        this.luogoController = tipoVisitaController.getLuogoController();
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Visualizza elenco luoghi con visite associate");
        Panel panel = new Panel();

        List<Luogo> luoghi = luogoController.getLuoghi();

        for (Luogo luogo : luoghi) {
            int luogoId = luogo.getId();
            List<String> titoliTipiVisita = tipoVisitaController.getTitoliByLuogoId(luogoId);

            panel.addComponent(new Label(luogo.getNome()));

            String visitaText = titoliTipiVisita.isEmpty()
                    ? "\tNessun tipo di visita associato"
                    : "\t" + String.join(", ", titoliTipiVisita);

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
