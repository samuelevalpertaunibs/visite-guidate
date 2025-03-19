package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.unibs.controllers.ConfigController;

public class ConfigView {
    private final ConfigController controller;

    private final TextBox nomeComuneField;
    private final TextBox provinciaComuneField;
    private final TextBox regioneComuneField;
    private final TextBox numeroMaxPersone;

    private final Label erroreComuneLabel;
    private final Label erroreNumeroMaxLabel;

    private final Button aggiungiComuneButton;
    private final Button confermaButton;

    public ConfigView(ConfigController configController) {
        this.controller = configController;

        this.nomeComuneField = new TextBox();
        this.provinciaComuneField = new TextBox();
        this.regioneComuneField = new TextBox();
        this.numeroMaxPersone = new TextBox();
        this.erroreComuneLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.erroreNumeroMaxLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);

        this.aggiungiComuneButton = new Button("Aggiungi Comune", () -> {
            controller.aggiungiComune(nomeComuneField.getText(), provinciaComuneField.getText(),
                    regioneComuneField.getText());
        });

        this.confermaButton = new Button("Conferma", () -> {
            controller.conferma(numeroMaxPersone.getText());
        });
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Inizializzazione corpo dati");
        Panel panel = new Panel();

        panel.addComponent(new Label("Comune"));

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
        // // Pulsante che apre il popup di conferma
        // Button confirmButton = new Button("Mostra Conferma", () -> {

        // MessageDialogButton result = new MessageDialogBuilder(
        // controller.getGui(),
        // "Conferma",
        // "Sei sicuro di procedere?",

        // MessageDialogButton.No;

        // )

        // Azione in base alla risposta
        // if(result==MessageDialogButton.Yes)

        // {
        // MessageDialog.showMessageDialog(controller.getGui(), "Risultato", "Hai scelto
        // Sì!");
        // }else
        // {
        // MessageDialog.showMessageDialog(controller.getGui(), "Risultato", "Hai scelto
        // No!");
        // }
        // });

        panel.addComponent(confermaButton);

        window.setComponent(panel);
        return window;
    }

    // public void chiediConferma(String title, String bodu) {
    // MessageDialog result = new
    // MessageDialogBuilder().setTitle(title).setText(body).addButton()

    public void showPopupMessage(String message) {
        Window window = new BasicWindow("Comune aggiunto");

        // Create a label to display the message
        Panel panel = new Panel();
        panel.addComponent(new Label(message));

        // Add a button to close the window
        Button closeButton = new Button("Chiudi", new Runnable() {
            @Override
            public void run() {
                window.close(); // Close the window when clicked
            }
        });
        panel.addComponent(closeButton);

        window.setComponent(panel);

        // Add the window to the GUI
        controller.getGui().addWindowAndWait(window);
    }

    public void showConfirmMessage(String message) {
        Window window = new BasicWindow("Conferma");

        // Create a label with the confirmation message
        Panel panel = new Panel();

        panel.addComponent(new Label(message));
        panel.addComponent(new Label("Sei sicuro di voler procedere?"));

        // Create "Yes" and "No" buttons
        Button yesButton = new Button("Sì", new Runnable() {
            @Override
            public void run() {
                window.close();
                controller.haConfermato();
            }
        });

        Button noButton = new Button("No", new Runnable() {
            @Override
            public void run() {
                window.close();
                controller.nonConferma();
            }
        });

        // Add buttons to the panel
        panel.addComponent(yesButton);
        panel.addComponent(noButton);

        window.setComponent(panel);

        // Show the confirmation window
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
