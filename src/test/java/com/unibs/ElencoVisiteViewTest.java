package com.unibs;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.unibs.controllers.VisitaController;
import com.unibs.services.VisitaService;

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
            VisitaService visitaService = new VisitaService();
            VisitaController controller = new VisitaController(gui, visitaService); // se richiede il GUI

            controller.apriVisualizzaVisitePerTipologia();

            screen.stopScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
