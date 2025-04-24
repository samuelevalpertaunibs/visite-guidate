package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.ConfigController;

import java.util.List;

public class ModificaNumeroMaxView {
    private final TextBox numeroMassimoTextBox;
    private final Label numeroMaxAttualeLabel;
    private final ConfigController configController;
    private final Label feedbackLabel;

    public ModificaNumeroMaxView(ConfigController configController) {
        this.configController = configController;
        numeroMassimoTextBox = new TextBox();
        numeroMaxAttualeLabel = new Label("");
        feedbackLabel = new Label("");
    }

    public Window creaFinestra(int numeroMassimoAttuale) {
        pulisciCampi();
        Window window = new BasicWindow("Modifica Configurazione");
        Panel panel = new Panel();

        numeroMaxAttualeLabel.setText("Numero massimo attuale: " + numeroMassimoAttuale);

        Button confermaButton = new Button("Conferma", () -> {
            String nuovoNumeroMax = numeroMassimoTextBox.getText();
            configController.setNumeroMaxPersone(nuovoNumeroMax);
        });

        // Chiude la finestra
        Button esciButton = new Button("Esci", window::close);

        panel.addComponent(numeroMaxAttualeLabel);
        panel.addComponent(new Label("Inserisci il nuovo numero massimo di persone:"));
        panel.addComponent(numeroMassimoTextBox);
        panel.addComponent(feedbackLabel);
        panel.addComponent(confermaButton);
        panel.addComponent(esciButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
        return window;
    }

    public void mostraErrore(String messaggio) {
        feedbackLabel.setText(messaggio);
        feedbackLabel.setForegroundColor(TextColor.ANSI.RED);
    }

    public void mostraSuccesso(String messaggio) {
        feedbackLabel.setText(messaggio);
        feedbackLabel.setForegroundColor(TextColor.ANSI.GREEN);
    }

    public void aggiornaNumeroAttuale(String numeroMax) {
        numeroMaxAttualeLabel.setText("Numero massimo attuale: " + numeroMax);
    }

    public void pulisciCampi() {
        numeroMassimoTextBox.setText("");
        numeroMaxAttualeLabel.setText("");
        feedbackLabel.setText("");
    }


}
