package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.LoginController;
import com.unibs.models.Utente;
import java.util.List;

import static com.googlecode.lanterna.TerminalTextUtils.getWordWrappedText;

public class CambioPasswordView {

    private final Window window;
    private final TextBox passwordField;
    private final TextBox confirmPasswordField;
    private final Label errorLabel;
    private final LoginController controller;
    private final Utente utente;
    private final Button confermaButton;
    private int defaultWindowRows;
    private int defaultWindowCols;


    public CambioPasswordView(LoginController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;
        passwordField = new TextBox(new TerminalSize(18, 1)).setMask('*');
        confirmPasswordField = new TextBox(new TerminalSize(18, 1)).setMask('*');
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED).setPreferredSize(new TerminalSize(18, 1));
        confermaButton = new Button("Conferma", this::onConferma);
        window = new BasicWindow("Cambio Password");
    }

    public Window creaFinestra() {
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

    private void onConferma() {
        mostraErrore("");
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        controller.updatePassword(utente, newPassword,  confirmPassword);
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

    public void close() {
        window.close();
    }
}
