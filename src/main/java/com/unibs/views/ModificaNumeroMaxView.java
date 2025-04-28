package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

public class ModificaNumeroMaxView {
    private final TextBox numeroMassimoTextBox;
    private final Label numeroMaxAttualeLabel;
    private final Label feedbackLabel;
    private final Button confermaButton;

    public ModificaNumeroMaxView() {
        confermaButton = new Button("Conferma");
        numeroMassimoTextBox = new TextBox();
        numeroMaxAttualeLabel = new Label("");
        feedbackLabel = new Label("");
    }

    private Window creaFinestra() {
        Window window = new BasicWindow("Modifica Configurazione");
        Panel panel = new Panel();
        panel.addComponent(numeroMaxAttualeLabel);
        panel.addComponent(new Label("Inserisci il nuovo numero massimo di persone:"));
        panel.addComponent(numeroMassimoTextBox);
        panel.addComponent(feedbackLabel);
        panel.addComponent(confermaButton);

        Button esciButton = new Button("Esci", window::close);
        panel.addComponent(esciButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
        return window;
    }

    public void mostra(WindowBasedTextGUI gui) {
        Window window = creaFinestra();
        gui.addWindowAndWait(window);
    }

    public void mostraErrore(String messaggio) {
        feedbackLabel.setText(messaggio);
        feedbackLabel.setForegroundColor(TextColor.ANSI.RED);
        numeroMassimoTextBox.takeFocus();
    }

    public void mostraSuccesso(String messaggio) {
        feedbackLabel.setText(messaggio);
        feedbackLabel.setForegroundColor(TextColor.ANSI.BLACK);
    }

    public void aggiornaNumeroAttuale(int numeroMax) {
        numeroMaxAttualeLabel.setText("Numero massimo attuale: " + numeroMax);
    }

    public Button getConfermaButton() {
        return confermaButton;
    }

    public String getNumeroMaxInserito() {
        return numeroMassimoTextBox.getText();
    }

    public void pulisci() {
        numeroMassimoTextBox.setText("");
        numeroMassimoTextBox.takeFocus();
    }
}
