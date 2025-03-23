package com.unibs.views;

import com.unibs.controllers.ConfigController;
import com.unibs.controllers.LuogoController;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.util.function.Consumer;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.TextColor;

public class AggiungiLuogoView {
    private final TextBox nomeField;
    private final TextBox descrizioneField;
    private Button selezionaComuneButton;
    private Comune comuneSelezionato = null;
    private Luogo luogo = null;
    private final Label errorLabel;
    private final Button aggiungiButton;

    public AggiungiLuogoView(LuogoController luogoController, ConfigController initController,
            Consumer<Luogo> onLuogoAdded) {
        this.nomeField = new TextBox();
        this.descrizioneField = new TextBox();
        this.errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);

        aggiungiButton = new Button("Aggiungi", () -> {
            String nome = nomeField.getText();
            String descrizione = descrizioneField.getText();

            try {
                this.luogo = luogoController.aggiungiLuogo(nome, descrizione, comuneSelezionato);

                if (onLuogoAdded != null) {
                    onLuogoAdded.accept(this.luogo); // Passa il luogo alla view chiamante
                }

                initController.getGui().getActiveWindow().close();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        });

        this.selezionaComuneButton = new Button("Nessun comune selezionato", () -> {
            SelezionaComuneView selezionaComuneView = new SelezionaComuneView(initController);
            initController.getGui().addWindowAndWait(selezionaComuneView.creaFinestra());

            Comune comuneScelto = selezionaComuneView.getComuneSelezionato();
            if (comuneScelto != null) {
                comuneSelezionato = comuneScelto;
                selezionaComuneButton.setLabel(comuneSelezionato.getNome());
                aggiungiButton.takeFocus();

            }
        });

    }

    // Metodo per creare la finestra di aggiunta del luogo
    public Window creaFinestra() {
        Window window = new BasicWindow("Aggiungi Luogo");

        Panel panel = new Panel();
        panel.addComponent(new Label("Nome del Luogo"));
        panel.addComponent(nomeField);

        panel.addComponent(new Label("Descrizione del Luogo"));
        panel.addComponent(descrizioneField);

        panel.addComponent(new Label(""));
        panel.addComponent(selezionaComuneButton);

        panel.addComponent(errorLabel);
        panel.addComponent(aggiungiButton);

        window.setComponent(panel);
        return window;
    }

    private void showError(String message) {
        this.errorLabel.setText(message);
    }

}
