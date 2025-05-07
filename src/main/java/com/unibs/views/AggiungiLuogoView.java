package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

public class AggiungiLuogoView {
    private Window window;
    private final TextBox nomeField;
    private final TextBox descrizioneField;
    private final Button selezionaComuneButton;
    private final Label errorLabel;
    private final Button aggiungiButton;
    private final Label labelComune;
    private final Panel panel;
    private final EmptySpace emptySpace;

    public AggiungiLuogoView() {
        this.nomeField = new TextBox();
        this.descrizioneField = new TextBox();
        this.errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.labelComune = new Label("Seleziona il comune di appartenenza");
        this.panel = new Panel();
        this.emptySpace = new EmptySpace();

        aggiungiButton = new Button("Aggiungi");
        selezionaComuneButton = new Button("Nessun comune selezionato");
    }

    public void creaFinestra() {
        window = new BasicWindow("Aggiungi un luogo");

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
    }

    public void mostra(WindowBasedTextGUI gui) {
        creaFinestra();
        gui.addWindowAndWait(window);
    }

    public void mostraErrore(String message) {
        panel.addComponent(7, errorLabel);
        panel.addComponent(8, emptySpace);
        errorLabel.setText(message);
    }

    public Button getSelezionaComuneButton() {
        return selezionaComuneButton;
    }

    public Button getAggiungiLuogoButton() {
        return aggiungiButton;
    }

    public String getNome() {
        return nomeField.getText();
    }

    public String getDescrizione() {
        return descrizioneField.getText();
    }

    public void chiudi() {
        window.close();
    }

    public void focusAggiungiLuogoButton() {
        aggiungiButton.takeFocus();
    }
}
