package com.unibs.views;
import com.googlecode.lanterna.gui2.*;

import java.util.List;


public class RegimeNonAttivoView {

    public Window creaFinestra() {
        Window window = new BasicWindow("ATTENZIONE");

        Panel panel = new Panel();
        panel.addComponent(new Label("L'applicazione non è ancora a regime di utilizzo!\nSi prega di riprovare nei prossimi giorni."));

        Button closeButton = new Button("Chiudi", window::close);
        panel.addComponent(closeButton);
        window.setHints(List.of(Window.Hint.CENTERED, Window.Hint.MENU_POPUP));
        window.setComponent(panel);
        return window;
    }
}
