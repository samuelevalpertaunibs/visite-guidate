package com.unibs;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.unibs.controllers.LoginController;
import com.unibs.utils.LoggerConfig;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {

            LoggerConfig.setupLogger();

            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            AppContext.SCREEN_WIDTH = screen.getTerminalSize().getColumns();
            AppContext.SCREEN_HEIGHT = screen.getTerminalSize().getRows();

            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);
            LoginController loginController = new LoginController(gui);

            gui.addWindowAndWait(loginController.getView());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
