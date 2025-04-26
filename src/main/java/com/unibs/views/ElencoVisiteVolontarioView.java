package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.unibs.controllers.TipoVisitaController;
import com.unibs.models.Luogo;
import com.unibs.models.TipoVisita;
import com.unibs.models.Visita;
import com.unibs.models.Volontario;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ElencoVisiteVolontarioView {
    private final Volontario volontario;
    private final TipoVisitaController controller;

    public ElencoVisiteVolontarioView(Volontario volontario, TipoVisitaController controller) {
        this.volontario = volontario;
        this.controller = controller;
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Tipi di visita associati a " + volontario.getUsername());
        Panel panel = new Panel();

//        try {
//            List<TipoVisita> tipiVisita = controller.getVisiteByVolontario(volontario.getId());
//            for (TipoVisita v : tipiVisita) {
//                panel.addComponent(new Label(v.toString()));
//            }
//        } catch (Exception e) {
//            panel.addComponent(new Label(e.getMessage()));
//        }
//
//        Button closeButton = new Button("Chiudi", window::close);
//        panel.addComponent(closeButton);

        try {
            List<TipoVisita> tipiVisita = controller.getVisiteByVolontario(volontario.getId());

            if (tipiVisita.isEmpty()) {
                panel.addComponent(new Label("Nessuna visita disponibile."));
            } else {
                AtomicInteger currentIndex = new AtomicInteger(0);
                Label visitaLabel = new Label(tipiVisita.get(0).toString());
                Label counterLabel = new Label("Visita 1 di " + tipiVisita.size());

                Button nextButton = new Button("Successivo", () -> {
                    int index = currentIndex.incrementAndGet();
                    if (index >= tipiVisita.size()) {
                        currentIndex.set(0);
                        index = 0;
                    }
                    visitaLabel.setText(tipiVisita.get(index).toString());
                    counterLabel.setText("Visita " + (index + 1) + " di " + tipiVisita.size());
                });

                Button closeButton = new Button("Chiudi", window::close);

                panel.addComponent(counterLabel);
                panel.addComponent(new EmptySpace());
                panel.addComponent(visitaLabel);
                panel.addComponent(new EmptySpace());
                panel.addComponent(nextButton);
                panel.addComponent(closeButton);
            }

        } catch (Exception e) {
            panel.addComponent(new Label("Errore: " + e.getMessage()));
        }


        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);

        return window;
    }
}
