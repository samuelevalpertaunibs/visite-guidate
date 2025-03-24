package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.ConfigController;

import java.util.Arrays;

public class InitConfigView {
    private final ConfigController controller;
    private final TextBox nomeComuneField;
    private final TextBox provinciaComuneField;
    private final TextBox regioneComuneField;
    private final TextBox numeroMaxPersone;

    private final Label erroreComuneLabel;
    private final Label erroreNumeroMaxLabel;

    private final Button aggiungiComuneButton;
    private final Button confermaButton;

    public InitConfigView(ConfigController configController) {
        this.controller = configController;

        this.nomeComuneField = new TextBox();
        this.provinciaComuneField = new TextBox();
        this.regioneComuneField = new TextBox();
        this.numeroMaxPersone = new TextBox();
        this.erroreComuneLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.erroreNumeroMaxLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.aggiungiComuneButton = new Button("Aggiungi Comune", () -> controller.aggiungiComune(nomeComuneField.getText(), provinciaComuneField.getText(),
                regioneComuneField.getText()));

        this.confermaButton = new Button("Conferma", () -> controller.conferma(numeroMaxPersone.getText()));
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Inizializzazione corpo dati");
        Panel panel = new Panel();

        panel.addComponent(new Label("Inserisci un comune da aggiungere all'ambito territoriale"));

        panel.addComponent(new Label("Nome"));
        panel.addComponent(nomeComuneField);

        panel.addComponent(new Label("Provincia"));
        panel.addComponent(provinciaComuneField);

        panel.addComponent(new Label("Regione"));
        panel.addComponent(regioneComuneField);

        panel.addComponent(erroreComuneLabel);
        panel.addComponent(aggiungiComuneButton);

        panel.addComponent(new Label(
                "Numero massimo di persone che un fruitore può iscrivere ad una iniziativa mediante singola iscrizione"));

        panel.addComponent(numeroMaxPersone);
        panel.addComponent(erroreNumeroMaxLabel);

        panel.addComponent(confermaButton);

        window.setComponent(panel);
        window.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        return window;
    }


    public void showPopupMessage(String title, String message) {
        Window window = new BasicWindow(title);

        Panel panel = new Panel();
        panel.addComponent(new Label(message));

        Button closeButton = new Button("Chiudi", window::close);
        panel.addComponent(closeButton);

        window.setComponent(panel);

        controller.getGui().addWindowAndWait(window);
    }

    public void showConfirmMessage(String message) {
        Window window = new BasicWindow("Conferma");

        // Create a label with the confirmation message
        Panel panel = new Panel();

        panel.addComponent(new Label(message));
        panel.addComponent(new Label("Sei sicuro di voler procedere?"));

        // Create "Yes" and "No" buttons
        Button yesButton = new Button("Sì", () -> {
            window.close();
            controller.haConfermato();
        });

        Button noButton = new Button("No", () -> {
            window.close();
            controller.nonConferma();
        });

        // Add buttons to the panel
        panel.addComponent(yesButton);
        panel.addComponent(noButton);

        window.setComponent(panel);
        controller.getGui().addWindowAndWait(window);
    }

    public void showComuneErrorMessage(String message) {
        erroreComuneLabel.setText(message);
    }

    public void resetComune() {
        nomeComuneField.setText("");
        provinciaComuneField.setText("");
        regioneComuneField.setText("");
        erroreComuneLabel.setText("");
    }

    public void showNumeroMaxErrorMessage(String message) {
        erroreNumeroMaxLabel.setText(message);
    }

    public void resetNumeroMax() {
        numeroMaxPersone.setText("");
    }

    public void resetError() {
        erroreComuneLabel.setText("");
        erroreNumeroMaxLabel.setText("");
    }

    public void moveCursorToNumeroMax() {
        numeroMaxPersone.takeFocus();
    }

    public void moveCursorToComune() {
        nomeComuneField.takeFocus();
    }
}
