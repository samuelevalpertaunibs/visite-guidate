package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.models.Luogo;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SelezionaLuogoView {
    private Window window;
    private final Button aggiungiLuogoButton;
    private final AtomicReference<Luogo> luogoSelezionato;

    public SelezionaLuogoView() {
        this.luogoSelezionato = new AtomicReference<>(null);
        this.aggiungiLuogoButton = new Button("+ Aggiungi Luogo");
    }

    public Button getAggiungiLuogoButton() {
        return aggiungiLuogoButton;
    }

    private void creaFinestra(List<Luogo> luoghi) {
        window = new BasicWindow("Seleziona luogo");
        Panel panel = new Panel();

        for (Luogo luogo : luoghi) {
            panel.addComponent(new Button(luogo.getNome(), () -> {
                luogoSelezionato.set(luogo);
                window.close();
            }));
        }

        panel.addComponent(aggiungiLuogoButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
    }

    public Luogo mostra(WindowBasedTextGUI gui, List<Luogo> luoghi) {
        creaFinestra(luoghi);
        gui.addWindowAndWait(window);
        return luogoSelezionato.get();
    }

    public void setLuogo(Luogo luogo) {
        luogoSelezionato.set(luogo);
    }

    public void chiudi() {
        window.close();
    }
}