package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.unibs.controllers.ConfigController;
import com.unibs.controllers.LuogoController;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.util.List;
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
    private final Label labelComune;
    private final Panel panel;
    private final EmptySpace emptySpace;

    public AggiungiLuogoView(LuogoController luogoController, ConfigController initController,
            Consumer<Luogo> onLuogoAdded) {
        this.nomeField = new TextBox();
        this.descrizioneField = new TextBox();
        this.errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.labelComune = new Label("Seleziona il comune di appartenenza");
        this.panel = new Panel();
        this.emptySpace = new EmptySpace();

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
                mostraErrore(e.getMessage());
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
        Window window = new BasicWindow("Aggiungi un luogo");

        nomeField.setPreferredSize(new TerminalSize(40, 1));
        descrizioneField.setPreferredSize(new TerminalSize(40, 1));

        panel.addComponent(new Label("Nome"));
        panel.addComponent(nomeField);
        panel.addComponent(new Label("Descrizione"));
        panel.addComponent(descrizioneField);

        panel.addComponent(labelComune);
        panel.addComponent(selezionaComuneButton);
        panel.addComponent(new EmptySpace());
        panel.addComponent(aggiungiButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
        return window;
    }

    private void mostraErrore(String message) {
        panel.addComponent(7, errorLabel);
        panel.addComponent(8, emptySpace);
        errorLabel.setText(message);
    }

}
