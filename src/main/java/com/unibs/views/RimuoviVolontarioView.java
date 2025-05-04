package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.util.List;
import java.util.function.Consumer;

public class RimuoviVolontarioView {

    private final Window window;
    private final Panel panel;
    private Consumer<String> onVolontarioSelected;

    public RimuoviVolontarioView() {
        this.window = new BasicWindow("Rimuovi volontario");
        this.panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
    }

    public void mostra(WindowBasedTextGUI gui) {
        gui.addWindowAndWait(window);
    }

    public void setVolontari(List<String> volontari) {
        panel.removeAllComponents();

        for (String volontario : volontari) {
            Button btn = new Button(volontario, () -> {
                if (onVolontarioSelected != null) {
                    onVolontarioSelected.accept(volontario);
                }
            });
            panel.addComponent(btn);
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Chiudi", window::close));

        panel.invalidate();
    }

    public void setOnVolontarioSelected(Consumer<String> listener) {
        this.onVolontarioSelected = listener;
    }

    public void chiudi() {
        window.close();
    }
}