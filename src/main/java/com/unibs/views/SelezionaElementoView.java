package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.utils.ElementoSelezionabile;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SelezionaElementoView<T extends ElementoSelezionabile> {
    private final AtomicReference<T> elementoSelezionato = new AtomicReference<>();
    private Window window;

    private void creaFinestra(List<T> elementi, String titolo) {
        window = new BasicWindow(titolo);
        Panel panel = new Panel();

        for (T elemento : elementi) {
            panel.addComponent(new Button(elemento.getPlaceHolder(), () -> {
                elementoSelezionato.set(elemento);
                window.close();
            }));
        }

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
    }

    public T mostra(WindowBasedTextGUI gui, List<T> elementi, String titolo) {
        creaFinestra(elementi, titolo);
        gui.addWindowAndWait(window);
        return elementoSelezionato.get();
    }

    public void chiudi() {
        window.close();
    }
}
