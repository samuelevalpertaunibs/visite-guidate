package com.unibs;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.VirtualScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.unibs.controllers.LoginController;
import com.unibs.services.ServiceFactory;
import com.unibs.utils.LoggerConfig;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            LoggerConfig.setupLogger();
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            // Avvolgi manualmente lo screen in un VirtualScreen
            VirtualScreen virtualScreen = new VirtualScreen(screen);

            // Ora usa il VirtualScreen per creare il MultiWindowTextGUI
            MultiWindowTextGUI gui = new MultiWindowTextGUI(virtualScreen);

            AppContext.SCREEN_WIDTH = screen.getTerminalSize().getColumns();
            AppContext.SCREEN_HEIGHT = screen.getTerminalSize().getRows();

            ServiceFactory serviceFactory = new ServiceFactory();

            LoginController loginController = new LoginController(gui, serviceFactory);
            loginController.apriLogin(gui);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
