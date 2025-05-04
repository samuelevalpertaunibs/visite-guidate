package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.models.MenuOption;

import java.util.List;

public class MenuView {
    private final WindowBasedTextGUI tui;
    private final Label errorLabel;
    private Window window;

    // Costruttore della view che riceve le opzioni del menu
    public MenuView(WindowBasedTextGUI tui) {
        this.tui = tui;
        errorLabel = new Label("");
    }

    // Mostra il menu con le opzioni dinamiche
    public void mostraMenu(List<MenuOption> options, String title, boolean exitButtonEnabled) {
        window = new BasicWindow(title);

        Panel menuPanel = new Panel();
        menuPanel.addComponent(new EmptySpace());
        menuPanel.setLayoutManager(new GridLayout(1));

        // Aggiungi i bottoni per ogni opzione
        for (MenuOption option : options) {
            menuPanel.addComponent(createMenuButton(option));
        }

        if (exitButtonEnabled) {
            menuPanel.addComponent(new Button("Esci", window::close));
            menuPanel.addComponent(errorLabel);
        }

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(menuPanel);

        // Avvia la finestra
        tui.addWindowAndWait(window);
    }

    // Crea un pulsante per il menu basato su una MenuOption
    private Button createMenuButton(MenuOption option) {
        return new Button(option.label(), () -> {
            option.action().accept(null);  // Esegui l'azione quando il bottone è premuto
        });
    }

    public void mostraErrore(String e) {
        errorLabel.setText(e);
    }

    public void aggiornaMenu(List<MenuOption> menuOptions, String s, boolean exitButtonEnabled) {
        window.close();
        mostraMenu(menuOptions, s, exitButtonEnabled);
    }

    public void close() {
        window.close();
    }
}
