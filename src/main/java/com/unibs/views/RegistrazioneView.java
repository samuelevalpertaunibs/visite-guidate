package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

import static com.googlecode.lanterna.TerminalTextUtils.getWordWrappedText;

public class RegistrazioneView {
    private final TextBox usernameField;
    private final TextBox passwordField;
    private final TextBox confermaPasswordField;
    private final com.googlecode.lanterna.gui2.Button registratiButton;
    private final com.googlecode.lanterna.gui2.Label errorLabel;
    // Le successive due variabili sono equivalenti a window.getSize().get{Rows,Columns}()
    private int defaultWindowRows;
    private int defaultWindowCols;
    final BasicWindow window;

    public RegistrazioneView() {
        usernameField = new TextBox(new TerminalSize(18, 1));
        passwordField = new TextBox(new TerminalSize(18, 1)).setMask('*'); // Maschera la password
        confermaPasswordField = new TextBox(new TerminalSize(18, 1)).setMask('*'); // Maschera la password
        errorLabel = new com.googlecode.lanterna.gui2.Label("").setForegroundColor(TextColor.ANSI.RED);
        registratiButton = new com.googlecode.lanterna.gui2.Button("Registrati");
        window = new BasicWindow("Registrazione");
    }

    private void creaFinestra() {
        Panel panel = new Panel();

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Username"));
        panel.addComponent(usernameField);

        panel.addComponent(new Label("Password"));
        panel.addComponent(passwordField);
        panel.addComponent(new Label("Conferma password"));
        panel.addComponent(confermaPasswordField);

        panel.addComponent(new EmptySpace());
        panel.addComponent(registratiButton);
        panel.addComponent(new Button("Chiudi", window::close));
        panel.addComponent(new EmptySpace());
        panel.addComponent(errorLabel);


        TerminalSize defaultSize = panel.calculatePreferredSize().withRelative(4, 0);
        defaultWindowCols = defaultSize.getColumns();
        defaultWindowRows = defaultSize.getRows();

        window.setHints(List.of(Window.Hint.CENTERED));
        window.setFixedSize(defaultSize);
        window.setComponent(panel);
    }

    public void mostra(WindowBasedTextGUI gui) {
        creaFinestra();
        gui.addWindowAndWait(window);
    }

    public Button getRegistratiButton() {
        return registratiButton;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public String getConfermaPassword() {
        return confermaPasswordField.getText();
    }

    // Questo metodo è diverso dal normale setLabel perche questa finestra ha una dimensione fisse "piccola"
    public void mostraErrore(String message) {
        int errorRows = getWordWrappedText(defaultWindowCols - 2, message).size();
        errorLabel.setPreferredSize(new TerminalSize(defaultWindowCols - 2, errorRows));
        errorLabel.setText(message);
        window.setFixedSize(new TerminalSize(defaultWindowCols, defaultWindowRows + errorRows));
    }

    public void chiudi() {
        window.close();
    }
}
