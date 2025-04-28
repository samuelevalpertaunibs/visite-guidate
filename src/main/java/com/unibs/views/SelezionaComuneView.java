package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.unibs.models.Comune;

public class SelezionaComuneView {
    private final AtomicReference<Comune> comuneSelezionato;
    private Window window;

    public SelezionaComuneView() {
        this.comuneSelezionato = new AtomicReference<>(null);
    }

    public Window creaFinestra(List<Comune> comuni) {
        window = new BasicWindow("Seleziona un Comune");
        Panel panel = new Panel();

        for (Comune comune : comuni) {
            panel.addComponent(new Button(comune.getNome(), () -> {
                comuneSelezionato.set(comune);
                window.close();
            }));
        }

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
        return window;
    }

    public Comune mostra(WindowBasedTextGUI gui, List<Comune> comuni) {
        creaFinestra(comuni);
        gui.addWindowAndWait(window);
        return comuneSelezionato.get();
    }
}
