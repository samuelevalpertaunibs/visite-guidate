package com.unibs;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;

import java.util.Arrays;

public class LoginView extends View {
    private final TextBox usernameField;
    private final TextBox passwordField;
    private final Button loginButton;
    private final LoginController controller;;

    public LoginView(LoginController loginController) {
        this.controller = loginController;

        usernameField = new TextBox();
        passwordField = new TextBox().setMask('*'); // Maschera la password

        loginButton = new Button("Accedi", () -> {
            controller.verificaCredenziali(usernameField.getText(), passwordField.getText());
        });
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Login");
        Panel panel = new Panel();

        panel.addComponent(new Label("Username:"));
        panel.addComponent(usernameField);

        panel.addComponent(new Label("Password:"));
        panel.addComponent(passwordField);

        panel.addComponent(loginButton);

        window.setComponent(panel);
        return window;
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
                resetLogin();
            }
        });
        panel.addComponent(closeButton);

        window.setComponent(panel);

        // Add the window to the GUI
        controller.getGui().addWindowAndWait(window);
    }

    // Metodo per resettare la schermata di login
    public void resetLogin() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.takeFocus();
    }
}
