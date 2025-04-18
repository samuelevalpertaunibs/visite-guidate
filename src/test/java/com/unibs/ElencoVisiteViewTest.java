package com.unibs;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.unibs.controllers.VisitaController;
import com.unibs.views.ElencoVisiteView;

public class ElencoVisiteViewTest {

    public static void main(String[] args) {
        try {
            // Setup schermo
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            Screen screen = terminalFactory.createScreen();
            screen.startScreen();

            // GUI Lanterna
            WindowBasedTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace());

            // Controller e view
            VisitaController controller = new VisitaController(gui); // se richiede il GUI
            ElencoVisiteView view = new ElencoVisiteView(controller);

            Window window = view.creaFinestra();
            gui.addWindowAndWait(window);
            screen.stopScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
