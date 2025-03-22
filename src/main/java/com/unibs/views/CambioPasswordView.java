package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.LoginController;
import com.unibs.models.Utente;

public class CambioPasswordView {

    private final Window window;
    private final TextBox passwordField;
    private final TextBox confirmPasswordField;
    private final Label errorLabel;
    private final LoginController controller;
    private final Utente utente;

    public CambioPasswordView(LoginController controller, Utente utente) {
        this.controller = controller;
        this.utente = utente;
        passwordField = new TextBox().setMask('*');
        confirmPasswordField = new TextBox().setMask('*');
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        window = new BasicWindow("Cambio Password");

    }

    public Window creaFinestra() {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Nuova Password:"));

        panel.addComponent(passwordField);

        panel.addComponent(new Label("Conferma Password:"));
        panel.addComponent(confirmPasswordField);


        panel.addComponent(errorLabel);

        Button confermaButton = new Button("Conferma", this::onConferma);
        panel.addComponent(confermaButton);

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
        passwordField.takeFocus();
    }

    public void mostraErrore(String message) {
        this.errorLabel.setText(message);
    }

    public void close() {
        window.close();
    }
}
