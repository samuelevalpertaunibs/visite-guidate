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
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            resetCambioPassword();
            errorLabel.setText("I campi non possono essere vuoti.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Le password non coincidono.");
            resetCambioPassword();
            return;
        }

        controller.updatePassword(user, newPassword);
        window.close(); // Torna alla pagina principale
    }

    private void resetCambioPassword() {
        passwordField.setText("");
        confirmPasswordField.setText("");
        passwordField.takeFocus();
    }

    public void showPopupMessage(String message) {
        Window window = new BasicWindow("Errore");

        // Create a label to display the message
        Panel panel = new Panel();
        panel.addComponent(new Label(message));

        // Add a button to close the window
        Button closeButton = new Button("Chiudi", new Runnable() {
            @Override
            public void run() {
                window.close();  // Close the window when clicked
            }
        });
        panel.addComponent(closeButton);

        window.setComponent(panel);

        // Add the window to the GUI
        controller.getGui().addWindowAndWait(window);
    }
}
