package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

public class AggiungiVolontarioView {
    private Window window;
    private final TextBox usernameField;
    private final Label errorLabel;
    private final Button aggiungiButton;
    private final Panel panel;
    private final EmptySpace emptySpace;

    public AggiungiVolontarioView() {
        this.usernameField = new TextBox();
        this.errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.panel = new Panel();
        this.emptySpace = new EmptySpace();
        aggiungiButton = new Button("Aggiungi");
    }

    public void creaFinestra() {
        window = new BasicWindow("Aggiungi un nuovo volontario");

        panel.addComponent(new Label("Username"));
        panel.addComponent(usernameField);

        panel.addComponent(new EmptySpace());
        panel.addComponent(aggiungiButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
    }

    public void mostra(WindowBasedTextGUI gui) {
        creaFinestra();
        gui.addWindowAndWait(window);
    }

    public void mostraErrore(String message) {
        panel.addComponent(2, errorLabel);
        panel.addComponent(3, emptySpace);
        errorLabel.setText(message);
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public Button getAggiungiButton() {
        return aggiungiButton;
    }

    public void chiudi() {
        window.close();
    }
}
