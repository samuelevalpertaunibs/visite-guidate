package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.LoginController;
import java.util.List;

import static com.googlecode.lanterna.TerminalTextUtils.getWordWrappedText;

public class LoginView {
    private final TextBox usernameField;
    private final TextBox passwordField;
    private final Label errorLabel;
    private final Button loginButton;
    private final LoginController controller;
    private final BasicWindow window;
    // Le successive due variabili sono equivalenti a window.getSize().get{Rows,Columns}()
    private int defaultWindowRows;
    private int defaultWindowCols;

    public LoginView(LoginController loginController) {
        controller = loginController;
        usernameField = new TextBox(new TerminalSize(18, 1));
        passwordField = new TextBox(new TerminalSize(18, 1)).setMask('*'); // Maschera la password
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED).setPreferredSize(new TerminalSize(18, 1));
        loginButton = new Button("Accedi", () -> controller.verificaCredenziali(usernameField.getText(), passwordField.getText()));
        window = new BasicWindow("Login");
    }

    public Window creaFinestra() {
        resetLogin();
        Panel panel = new Panel();

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Username"));
        panel.addComponent(usernameField);

        panel.addComponent(new Label("Password"));
        panel.addComponent(passwordField);

        panel.addComponent(new EmptySpace());
        panel.addComponent(loginButton);
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

    // Metodo per resettare la schermata di login
    public void resetLogin() {
        passwordField.setText("");
        errorLabel.setText("");
        usernameField.takeFocus();
    }

    public void mostraErrore(String message) {
        int errorRows = getWordWrappedText(defaultWindowCols - 2, message).size();
        errorLabel.setPreferredSize(new TerminalSize(defaultWindowCols - 2 , errorRows));
        errorLabel.setText(message);
        window.setFixedSize(new TerminalSize(defaultWindowCols, defaultWindowRows + errorRows));
    }

    public void close() {
        window.close();
    }
}
