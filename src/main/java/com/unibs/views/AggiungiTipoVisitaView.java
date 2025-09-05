package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.models.CoppiaIdUsername;
import com.unibs.models.Giorno;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AggiungiTipoVisitaView {
    private static final int YES_INDEX = 0;
    private Window window;
    private final Label luogoLabel;
    private final TextBox titoloField;
    private final TextBox descrizioneField;
    private final TextBox indirizzoField;
    private final TextBox comuneField;
    private final TextBox provinciaField;
    private final TextBox dataInizioField;
    private final TextBox dataFineField;
    private final TextBox oraInizioField;
    private final TextBox durataField;
    private final TextBox numeroMinPartecipantiField;
    private final TextBox numeroMaxPartecipantiField;
    private final RadioBoxList<java.lang.String> entrataLibera;
    private final Label volontariSelezionatiLabel;
    private final Label giorniSelezionatiLabel;
    private final Label errorLabel;
    private final Label visiteLabel;

    private final Button aggiungiButton;
    private final Button fineButton;
    private final Button selezionaLuogoButton;
    private final Button associaGiorniButton;
    private final Button associaVolontariButton;

    private final Panel bottomPanel;
    private final Panel errorPanel;

    public Button getFineButton() {
        return fineButton;
    }

    public Button getSelezionaLuogoButton() {
        return selezionaLuogoButton;
    }

    public Button getAssociaGiorniButton() {
        return associaGiorniButton;
    }

    public Button getAssociaVolontariButton() {
        return associaVolontariButton;
    }

    public Button getAggiungiButton() {
        return aggiungiButton;
    }

    public AggiungiTipoVisitaView() {
        TerminalSize defaultSize = new TerminalSize(16, 1);

        luogoLabel = new Label("Seleziona un luogo");
        titoloField = new TextBox(defaultSize);
        descrizioneField = new TextBox(defaultSize);
        indirizzoField = new TextBox(defaultSize);
        comuneField = new TextBox(defaultSize);
        provinciaField = new TextBox(defaultSize);
        dataInizioField = new TextBox(defaultSize);
        dataFineField = new TextBox(defaultSize);
        oraInizioField = new TextBox(defaultSize);
        durataField = new TextBox(defaultSize);
        numeroMinPartecipantiField = new TextBox(defaultSize);
        numeroMaxPartecipantiField = new TextBox(defaultSize);

        entrataLibera = new RadioBoxList<>();
        entrataLibera.addItem("Sì");
        entrataLibera.addItem("No");
        entrataLibera.setCheckedItemIndex(YES_INDEX);

        volontariSelezionatiLabel = new Label("Nessun volontario selezionato");
        giorniSelezionatiLabel = new Label("Nessun giorno selezionato");
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        visiteLabel = new Label("Ancora nessuna visita aggiunta");

        aggiungiButton = new Button("Aggiungi"); // NESSUNA azione
        fineButton = new Button("Fine"); // NESSUNA azione
        selezionaLuogoButton = new Button("Nessun luogo selezionato"); // NESSUNA azione
        associaGiorniButton = new Button("Seleziona giorni"); // NESSUNA azione
        associaVolontariButton = new Button("Associa volontari"); // NESSUNA azione

        bottomPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        errorPanel = new Panel(new LinearLayout(Direction.VERTICAL));
    }

    private void creaFinestra() {
        window = new BasicWindow("INIZIALIZZAZIONE CORPO DATI - Aggiungi tipo visita");
        Panel mainPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel topPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel inputPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel recapPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel leftInputPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel rightInputPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Panel buttonPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        leftInputPanel.addComponent(luogoLabel);
        leftInputPanel.addComponent(selezionaLuogoButton);
        leftInputPanel.addComponent(new Label("Titolo"));
        leftInputPanel.addComponent(titoloField);
        leftInputPanel.addComponent(new Label("Descrizione"));
        leftInputPanel.addComponent(descrizioneField);
        leftInputPanel.addComponent(new Label("Data inizio (DD/MM)"));
        leftInputPanel.addComponent(dataInizioField);
        leftInputPanel.addComponent(new Label("Data fine (DD/MM)"));
        leftInputPanel.addComponent(dataFineField);
        leftInputPanel.addComponent(new Label("Ora inizio (HH:MM)"));
        leftInputPanel.addComponent(oraInizioField);
        leftInputPanel.addComponent(new Label("Durata (min)"));
        leftInputPanel.addComponent(durataField);
        leftInputPanel.addComponent(new Label("Numero minimo partecipanti"));
        leftInputPanel.addComponent(numeroMinPartecipantiField);
        leftInputPanel.addComponent(new Label("Numero massimo partecipanti"));
        leftInputPanel.addComponent(numeroMaxPartecipantiField);

        Panel puntoIncontroPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        puntoIncontroPanel.addComponent(new Label("Punto d'incontro"));
        puntoIncontroPanel.addComponent(new Label("Indirizzo"));
        puntoIncontroPanel.addComponent(indirizzoField);
        puntoIncontroPanel.addComponent(new Label("Comune"));
        puntoIncontroPanel.addComponent(comuneField);
        puntoIncontroPanel.addComponent(new Label("Provincia"));
        puntoIncontroPanel.addComponent(provinciaField);

        rightInputPanel.addComponent(puntoIncontroPanel);
        rightInputPanel.addComponent(new EmptySpace());
        rightInputPanel.addComponent(associaGiorniButton);
        rightInputPanel.addComponent(giorniSelezionatiLabel);
        rightInputPanel.addComponent(new EmptySpace());
        rightInputPanel.addComponent(new Label("Entrata libera?"));
        rightInputPanel.addComponent(entrataLibera);
        rightInputPanel.addComponent(new EmptySpace());
        rightInputPanel.addComponent(associaVolontariButton);
        rightInputPanel.addComponent(volontariSelezionatiLabel);

        buttonPanel.addComponent(aggiungiButton);
        buttonPanel.addComponent(fineButton);

        errorPanel.addComponent(errorLabel);
        errorPanel.addComponent(new EmptySpace());

        bottomPanel.addComponent(buttonPanel);
        inputPanel.addComponent(leftInputPanel);
        inputPanel.addComponent(new EmptySpace());
        inputPanel.addComponent(rightInputPanel);

        recapPanel.addComponent(visiteLabel);

        topPanel.addComponent(inputPanel.withBorder(Borders.singleLine("Nuovo tipo di visita")));
        topPanel.addComponent(recapPanel.withBorder(Borders.singleLine("Tipi di visita aggiunti")));

        mainPanel.addComponent(topPanel);
        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(bottomPanel);

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.EXPANDED));
    }

    public void mostra(WindowBasedTextGUI tui) {
        creaFinestra();
        tui.addWindowAndWait(window);
    }

    public void mostraErrore(java.lang.String message) {
        bottomPanel.addComponent(0, errorPanel);
        errorLabel.setText(message);
    }

    public void aggiornaVisite(java.lang.String message) {
        visiteLabel.setText(message);
    }

    public void clearAll() {
        luogoLabel.setText("Seleziona un luogo");
        titoloField.setText("");
        descrizioneField.setText("");
        indirizzoField.setText("");
        comuneField.setText("");
        provinciaField.setText("");
        dataInizioField.setText("");
        dataFineField.setText("");
        oraInizioField.setText("");
        durataField.setText("");
        numeroMinPartecipantiField.setText("");
        numeroMaxPartecipantiField.setText("");
        entrataLibera.setCheckedItemIndex(YES_INDEX);
        volontariSelezionatiLabel.setText("Nessun volontario selezionato");
        giorniSelezionatiLabel.setText("Nessun giorno selezionato");
        selezionaLuogoButton.setLabel("Nessun luogo selezionato");
        selezionaLuogoButton.setEnabled(true);
        errorLabel.setText("");
        bottomPanel.removeComponent(errorPanel);
    }

    public void chiudi() {
        window.close();
    }

    public void aggiornaGiorniSelezionati(Set<Giorno> giorniSelezionati) {
        if (giorniSelezionati == null || giorniSelezionati.isEmpty()) {
            giorniSelezionatiLabel.setText("Nessun giorno selezionato");
            return;
        }

        String testo = giorniSelezionati.stream()
                .sorted(Comparator.comparing(Giorno::getId))
                .map(Giorno::getNome)
                .collect(Collectors.joining(", "));

        giorniSelezionatiLabel.setText(testo);
    }

    public void aggiornaVolontariSelezionati(Set<CoppiaIdUsername> volontariSelezionati) {
        if (volontariSelezionati == null || volontariSelezionati.isEmpty()) {
            volontariSelezionatiLabel.setText("Nessun volontario selezionato");
            return;
        }


        java.lang.String testo = volontariSelezionati.stream()
                .sorted(Comparator.comparing(CoppiaIdUsername::getId))
                .map(CoppiaIdUsername::getUsername)
                .collect(Collectors.joining(", "));
        volontariSelezionatiLabel.setText(testo);
    }

    public java.lang.String getTitolo() {
        return titoloField.getText();
    }

    public java.lang.String getDescrizione() {
        return descrizioneField.getText();
    }

    public java.lang.String getIndirizzo() {
        return indirizzoField.getText();
    }

    public java.lang.String getComune() {
        return comuneField.getText();
    }

    public java.lang.String getProvincia() {
        return provinciaField.getText();
    }

    public java.lang.String getDataInizio() {
        return dataInizioField.getText();
    }

    public java.lang.String getDataFine() {
        return dataFineField.getText();
    }

    public java.lang.String getOraInizio() {
        return oraInizioField.getText();
    }

    public java.lang.String getDurata() {
        return durataField.getText();
    }

    public java.lang.String getNumeroMinPartecipanti() {
        return numeroMinPartecipantiField.getText();
    }

    public java.lang.String getNumeroMaxPartecipanti() {
        return numeroMaxPartecipantiField.getText();
    }

    public boolean getEntrataLibera() {
        return entrataLibera.isChecked(YES_INDEX);
    }

    public void focusTitolo() {
        titoloField.takeFocus();
    }

    public void aggiornaLuogo(java.lang.String testo) {
        luogoLabel.setText("Luogo selezionato");
        selezionaLuogoButton.setLabel(testo);
    }
}