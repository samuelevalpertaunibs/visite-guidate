package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.util.List;
import java.util.function.Consumer;

public class SelezionaTipoVisitaView {

    private final Window window;
    private final Panel panel;
    private Consumer<String> onTipoVisitaSelected;

    public SelezionaTipoVisitaView(String titolo) {
        this.window = new BasicWindow(titolo);
        this.panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
    }

    public void mostra(WindowBasedTextGUI gui) {
        gui.addWindowAndWait(window);
    }

    public void setTitoliTipiVisita(List<String> tvs) {
        panel.removeAllComponents();

        for (String tv : tvs) {
            Button btn = new Button(tv, () -> {
                if (onTipoVisitaSelected != null) {
                    onTipoVisitaSelected.accept(tv);
                }
            });
            panel.addComponent(btn);
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Chiudi", window::close));

        panel.invalidate();
    }

    public void setOnTipoVisitaSelected(Consumer<String> listener) {
        this.onTipoVisitaSelected = listener;
    }

    public void chiudi() {
        window.close();
    }
}