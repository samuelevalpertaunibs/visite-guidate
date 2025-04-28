package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import java.util.List;

import static com.googlecode.lanterna.TerminalTextUtils.getWordWrappedText;

public class CambioPasswordView {

    private final Window window;
    private final TextBox passwordField;
    private final TextBox confirmPasswordField;
    private final Label errorLabel;
    private final Button confermaButton;
    private int defaultWindowRows;
    private int defaultWindowCols;

    public CambioPasswordView() {
        passwordField = new TextBox(new TerminalSize(18, 1)).setMask('*');
        confirmPasswordField = new TextBox(new TerminalSize(18, 1)).setMask('*');
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED).setPreferredSize(new TerminalSize(18, 1));
        confermaButton = new Button("Conferma");
        window = new BasicWindow("Cambio Password");
    }

    private Window creaFinestra() {
        Panel panel = new Panel();

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Nuova Password:"));
        panel.addComponent(passwordField);

        panel.addComponent(new Label("Conferma Password:"));
        panel.addComponent(confirmPasswordField);

        panel.addComponent(new EmptySpace());
        panel.addComponent(confermaButton);
        panel.addComponent(new EmptySpace());
        panel.addComponent(errorLabel);

        TerminalSize defaultSize = panel.calculatePreferredSize().withRelative(4, 0);
        defaultWindowCols = defaultSize.getColumns();
        defaultWindowRows = defaultSize.getRows();

        window.setHints(List.of(Window.Hint.CENTERED));
        window.setFixedSize(defaultSize);
        window.setComponent(panel);
        return window;
    }

    public void resetCampi() {
        passwordField.setText("");
        confirmPasswordField.setText("");
        errorLabel.setText("");
        passwordField.takeFocus();
    }

    public void mostraErrore(String message) {
        int errorRows = getWordWrappedText(defaultWindowCols - 2, message).size();
        errorLabel.setPreferredSize(new TerminalSize(defaultWindowCols - 2, errorRows));
        errorLabel.setText(message);
        window.setFixedSize(new TerminalSize(defaultWindowCols, defaultWindowRows + errorRows));
    }

    public void chiudi() {
        window.close();
    }

    public void mostra(MultiWindowTextGUI gui) {
        Window window = creaFinestra();
        gui.addWindowAndWait(window);
    }

    public Button getConfermaButton() {
        return confermaButton;
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public String getConfermaPassword() {
        return confirmPasswordField.getText();
    }
}
