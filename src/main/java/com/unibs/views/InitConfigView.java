package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.AppContext;
import com.unibs.controllers.ConfigController;

import java.util.List;

public class InitConfigView {
    private final ConfigController controller;
    private final TextBox nomeComuneField;
    private final TextBox provinciaComuneField;
    private final TextBox regioneComuneField;
    private final TextBox numeroMaxField;

    private final Label erroreComuneLabel;
    private final Label erroreNumeroMaxLabel;

    private final Button aggiungiComuneButton;
    private final Button confermaButton;
    private final Button proseguiButton;
    private final Window window;
    private final Panel comunePanel;
    private final Panel numeroMaxPanel;
    private final EmptySpace emptySpace;
    private final Label ambitoTerritorialeLabel;

    public InitConfigView(ConfigController configController) {
        TerminalSize defaultSize = new TerminalSize(16, 1);
        this.controller = configController;
        this.nomeComuneField = new TextBox(defaultSize);
        this.provinciaComuneField = new TextBox(defaultSize);
        this.regioneComuneField = new TextBox(defaultSize);
        this.numeroMaxField = new TextBox(defaultSize);
        this.window = new BasicWindow("INIZIALIZZAZIONE CORPO DATI - Configurazione");
        this.erroreComuneLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.erroreNumeroMaxLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.aggiungiComuneButton = new Button("Aggiungi Comune", () -> controller.aggiungiComune(nomeComuneField.getText(), provinciaComuneField.getText(),
                regioneComuneField.getText()));
        this.comunePanel = new Panel();
        this.numeroMaxPanel = new Panel();
        this.proseguiButton = new Button("Prosegui", controller::confermaAmbito);
        this.confermaButton = new Button("Conferma", () -> controller.conferma(numeroMaxField.getText()));
        this.emptySpace = new EmptySpace();
        this.ambitoTerritorialeLabel = new Label("L'ambito territoriale è vuoto");
    }

    public Window creaFinestra() {
        comunePanel.addComponent(new Label("Inserisci un comune da aggiungere all'ambito territoriale"));
        comunePanel.addComponent(new Label("Nome"));
        comunePanel.addComponent(nomeComuneField);

        comunePanel.addComponent(new Label("Provincia"));
        comunePanel.addComponent(provinciaComuneField);

        comunePanel.addComponent(new Label("Regione"));
        comunePanel.addComponent(regioneComuneField);

        comunePanel.addComponent(new EmptySpace());
        comunePanel.addComponent(aggiungiComuneButton);

        comunePanel.addComponent(new EmptySpace());
        comunePanel.addComponent(ambitoTerritorialeLabel);

        comunePanel.addComponent(new EmptySpace());
        comunePanel.addComponent(proseguiButton);

        comunePanel.addComponent(confermaButton);

        String string = "Inserisci il numero massimo di persone che un fruitore può iscrivere ad una iniziativa mediante singola iscrizione";
        Label numeroMaxLabel = new Label(string);
        numeroMaxLabel.setPreferredSize(new TerminalSize(AppContext.SCREEN_WIDTH - 4, 2));
        numeroMaxPanel.addComponent(numeroMaxLabel);
        numeroMaxPanel.addComponent(numeroMaxField);
        numeroMaxPanel.addComponent(new EmptySpace());
        numeroMaxPanel.addComponent(confermaButton);

        window.setComponent(comunePanel);
        window.setHints(List.of(Window.Hint.FIT_TERMINAL_WINDOW, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        return window;
    }

    public void mostraErroreComune(String message) {
        comunePanel.addComponent(7, emptySpace);
        comunePanel.addComponent(8, erroreComuneLabel);
        erroreComuneLabel.setText(message);
    }

    public void resetComune() {
        nomeComuneField.setText("");
        provinciaComuneField.setText("");
        regioneComuneField.setText("");
        comunePanel.removeComponent(emptySpace);
        comunePanel.removeComponent(erroreComuneLabel);
    }

    public void mostraErroreNumeroMax(String message) {
        numeroMaxPanel.addComponent(3, erroreNumeroMaxLabel);
        numeroMaxPanel.addComponent(4, emptySpace);
        erroreNumeroMaxLabel.setText(message);
    }

    public void resetNumeroMax() {
        numeroMaxField.setText("");
    }

    public void moveCursorToNumeroMax() {
        numeroMaxField.takeFocus();
    }

    public void moveCursorToComune() {
        nomeComuneField.takeFocus();
    }

    public void mostraAmbito(String message) {
        ambitoTerritorialeLabel.setText(message);
    }

    public void mostraNumeroMaxPanel() {
        window.setComponent(numeroMaxPanel);
    }

    public void moveCursorToProsegui() {
        proseguiButton.takeFocus();
    }
}
