package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.LoginController;
import com.unibs.models.User;

public class CambioPasswordView {

    private final Window window;
    private final TextBox passwordField;
    private final TextBox confirmPasswordField;
    private final Label errorLabel;
    private final LoginController controller;
    private final User user;

    public CambioPasswordView(LoginController controller, User user) {
        this.controller = controller;
        this.user = user;
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
        controller.updatePassword(user, newPassword,  confirmPassword);
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
