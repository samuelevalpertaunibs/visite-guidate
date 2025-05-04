package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.models.Luogo;

import java.util.List;
import java.util.function.Consumer;

public class RimuoviLuogoView {

    private final Window window;
    private final Panel panel;
    private Consumer<Integer> onLuogoSelected;

    public RimuoviLuogoView() {
        this.window = new BasicWindow("Rimuovi Luogo");
        this.panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
    }

    public void mostra(WindowBasedTextGUI gui) {
        gui.addWindowAndWait(window);
    }

    public void setLuoghi(List<Luogo> luoghi) {
        panel.removeAllComponents();

        for (Luogo luogo : luoghi) {
            Button btn = new Button(luogo.getNome(), () -> {
                if (onLuogoSelected != null) {
                    onLuogoSelected.accept(luogo.getId());
                }
            });
            panel.addComponent(btn);
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Chiudi", window::close));

        panel.invalidate();
    }

    public void setOnLuogoSelected(Consumer<Integer> listener) {
        this.onLuogoSelected = listener;
    }

    public void chiudi() {
        window.close();
    }
}