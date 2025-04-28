package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.models.Luogo;

import java.util.ArrayList;
import java.util.List;

public class ElencoLuoghiConVisiteAssociateView {
    Panel panel;
    ArrayList<Component> components = new ArrayList<>();

    private Window creaFinestra() {
        Window window = new BasicWindow("Visualizza Elenco Volontari");
        panel = new Panel();

        for (Component c : components) {
            panel.addComponent(c);
        }

        Button closeButton = new Button("Chiudi", window::close);
        panel.addComponent(closeButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);

        return window;
    }

    public void aggiungiLuogo(Luogo luogo, List<String> titoliTipiVisita) {
        components.add(new Label("Luogo: " + luogo.getNome()));

        String visitaText = titoliTipiVisita.isEmpty()
                ? "Nessun tipo di visita associato"
                : "Visite associate: " + String.join(", ", titoliTipiVisita);

        components.add(new Label(visitaText));
        components.add(new EmptySpace());
    }

    public void mostra(WindowBasedTextGUI gui) {
        Window window = creaFinestra();
        gui.addWindowAndWait(window);
    }
}
