package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

import static com.googlecode.lanterna.TerminalTextUtils.getWordWrappedText;

public class LoginView {
    private final TextBox usernameField;
    private final TextBox passwordField;
    private final Label errorLabel;
    private final Button loginButton;
    private final BasicWindow window;
    // Le successive due variabili sono equivalenti a window.getSize().get{Rows,Columns}()
    private int defaultWindowRows;
    private int defaultWindowCols;

    public LoginView () {
        usernameField = new TextBox(new TerminalSize(18, 1));
        passwordField = new TextBox(new TerminalSize(18, 1)).setMask('*'); // Maschera la password
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED).setPreferredSize(new TerminalSize(18, 1));
        loginButton = new Button("Accedi");
        window = new BasicWindow("Login");
    }

    private Window creaFinestra() {
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

    // Questo metodo è diverso dal normale setLabel perche questa finestra ha una dimensione fisse "piccola"
    public void mostraErrore(String message) {
        int errorRows = getWordWrappedText(defaultWindowCols - 2, message).size();
        errorLabel.setPreferredSize(new TerminalSize(defaultWindowCols - 2 , errorRows));
        errorLabel.setText(message);
        window.setFixedSize(new TerminalSize(defaultWindowCols, defaultWindowRows + errorRows));
    }

    public void chiudi() {
        window.close();
    }

    public void mostra(WindowBasedTextGUI gui) {
        Window window = creaFinestra();
        gui.addWindowAndWait(window);
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

}
