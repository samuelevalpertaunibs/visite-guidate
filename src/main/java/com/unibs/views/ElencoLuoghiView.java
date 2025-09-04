package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.formatters.ComuneFormatter;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.util.List;

public class ElencoLuoghiView {

    private Window creaFinestra(List<Luogo> luoghi) {
        Window window = new BasicWindow("Visualizza elenco luoghi visitabili");

        Panel panel = new Panel();

        for (Luogo luogo : luoghi) {
            String nome = luogo.getNome();
            String descrizione = luogo.getDescrizione();
            Comune comune = luogo.getComune();
            ComuneFormatter comuneFormatter = new ComuneFormatter();
            String message = String.format("Nome: %s\nDescrizione: %s\nComune: %s", nome, descrizione, comuneFormatter.format(comune));
            panel.addComponent(new Label(message));
            panel.addComponent(new EmptySpace());
        }

        Button chiudiButton = new Button("Chiudi", window::close);
        panel.addComponent(chiudiButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);

        return window;
    }

    public void mostra(WindowBasedTextGUI gui, List<Luogo> luoghi) {
        Window window = creaFinestra(luoghi);
        gui.addWindowAndWait(window);
    }

}
