package com.unibs.views;

import java.util.List;
import java.util.ArrayList;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.LuogoController;
import com.unibs.controllers.TipoVisitaController;
import com.unibs.controllers.VolontariController;
import com.unibs.models.Volontario;

public class AggiungiTipoVisitaView {
    private final TipoVisitaController tipoVisitaController;
    private final VolontariController volontariController;
    private final SelezionaLuogoView selezionaLuogoView;
    private final LuogoController luogoController;

    private final Button selezionaLuogoButton;
    private final Button associaGiorniButton;
    private final Button associaVolontariButton;
    private final Button fineButton;
    private final Label luogoLabel;
    private final TextBox titoloField;
    private final TextBox descrizioneField;
    private final TextBox dataInizioField;
    private final TextBox dataFineField;
    private final TextBox oraInizioField;
    private final TextBox durataField;
    private final TextBox indirizzoField;
    private final TextBox comuneField;
    private final TextBox provinciaField;
    private final RadioBoxList<String> entrataLibera;
    private final TextBox numeroMinPartecipantiField;
    private final TextBox numeroMaxPartecipantiField;
    private final Label errorLabel;
    private final Label volontariSelezionatiLabel;
    private final Label giorniSelezionatiLabel;
    private final Panel bottomPanel;
    private final Panel errorPanel;
    private final Label visiteLabel;

    private final List<String> volontariSelezionati = new ArrayList<>();
    private final List<String> giorniSelezionati = new ArrayList<>();

    public AggiungiTipoVisitaView(TipoVisitaController tipoVisitaController) {
        this.tipoVisitaController = tipoVisitaController;
        this.luogoController = tipoVisitaController.getLuogoController();
        this.volontariController = tipoVisitaController.getVolontariController();
        this.selezionaLuogoView = new SelezionaLuogoView(luogoController);
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
        entrataLibera = new RadioBoxList<>();
        entrataLibera.addItem("Sì");
        entrataLibera.addItem("No");
        numeroMinPartecipantiField = new TextBox(defaultSize);
        numeroMaxPartecipantiField = new TextBox(defaultSize);
        volontariSelezionatiLabel = new Label("Nessun volontario selezionato");
        giorniSelezionatiLabel = new Label("Nessun giorno selezionato");
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        fineButton = new Button("Fine", tipoVisitaController::chiudiFinestraAggiungiTipoVisita);
        this.selezionaLuogoButton = new Button("Nessun luogo selezionato", this::apriPopupSelezioneLuogo);
        this.associaGiorniButton = new Button("Seleziona giorni", this::apriPopupSelezioneGiorni);
        this.associaVolontariButton = new Button("Associa volontari", this::apriPopupSelezioneVolontari);
        bottomPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        errorPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        visiteLabel = new Label("Ancora nessuna visita aggiunta");
    }

    private void apriPopupSelezioneLuogo() {
        selezionaLuogoView.setOnLuogoSelected((luogoSelezionato) -> {
            this.selezionaLuogoButton.setLabel(luogoSelezionato.getNome());
            this.selezionaLuogoButton.setEnabled(false);
        });

        tipoVisitaController.getGui().addWindowAndWait(selezionaLuogoView.creaFinestra());
        luogoLabel.setText("Luogo selezionato:");
        titoloField.takeFocus();
    }

    private void apriPopupSelezioneGiorni() {
        BasicWindow popupWindow = new BasicWindow("Seleziona giorni della settimana");
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        List<CheckBox> checkBoxList = new ArrayList<>();
        List<String> giorni = tipoVisitaController.getGiorniSettimana();

        for (String giorno : giorni) {
            CheckBox checkBox = new CheckBox(giorno);
            if (giorniSelezionati.contains(giorno))
                checkBox.setChecked(true);
            checkBoxList.add(checkBox);
            panel.addComponent(checkBox);
        }

        Button confermaButton = new Button("Conferma", () -> {
            giorniSelezionati.clear();
            for (CheckBox checkBox : checkBoxList) {
                if (checkBox.isChecked()) {
                    giorniSelezionati.add(checkBox.getLabel());
                }
            }
            giorniSelezionatiLabel.setText(String.join(", ", giorniSelezionati));
            popupWindow.close();
        });

        panel.addComponent(new EmptySpace());
        panel.addComponent(confermaButton);
        popupWindow.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        popupWindow.setComponent(panel);
        tipoVisitaController.getGui().addWindowAndWait(popupWindow);
    }

    private void apriPopupSelezioneVolontari() {
        BasicWindow popupWindow = new BasicWindow("Seleziona volontari");
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        List<CheckBox> checkBoxList = new ArrayList<>();
        List<Volontario> volontariDisponibili = volontariController.getAllVolontari(); // Ottiene la lista dal controller

        for (Volontario volontario : volontariDisponibili) {
            CheckBox checkBox = new CheckBox(volontario.getUsername());
            if (volontariSelezionati.contains(volontario.getUsername()))
                checkBox.setChecked(true);
            checkBoxList.add(checkBox);
            panel.addComponent(checkBox);
        }

        Button confermaButton = new Button("Conferma", () -> {
            volontariSelezionati.clear();
            for (CheckBox checkBox : checkBoxList) {
                if (checkBox.isChecked()) {
                    volontariSelezionati.add(checkBox.getLabel());
                }
            }
            volontariSelezionatiLabel.setText(String.join(", ", volontariSelezionati));
            popupWindow.close();
        });

        panel.addComponent(new EmptySpace());
        panel.addComponent(confermaButton);
        popupWindow.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        popupWindow.setComponent(panel);
        tipoVisitaController.getGui().addWindowAndWait(popupWindow);
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("INIZIALIZZAZIONE CORPO DATI - Aggiungi tipo visita");
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
        leftInputPanel.addComponent(new Label("Data inizio (dd/MM)"));
        leftInputPanel.addComponent(dataInizioField);
        leftInputPanel.addComponent(new Label("Data fine (dd/MM)"));
        leftInputPanel.addComponent(dataFineField);
        leftInputPanel.addComponent(new Label("Ora inizio (HH:mm)"));
        leftInputPanel.addComponent(oraInizioField);
        leftInputPanel.addComponent(new Label("Durata (min)"));
        leftInputPanel.addComponent(durataField);
        leftInputPanel.addComponent(new Label("Numero minimo partecipanti"));
        leftInputPanel.addComponent(numeroMinPartecipantiField);
        leftInputPanel.addComponent(new Label("Numero massimo partecipanti"));
        leftInputPanel.addComponent(numeroMaxPartecipantiField);

        Panel puntoIncontroPanel = new Panel();
        puntoIncontroPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
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
        entrataLibera.setCheckedItemIndex(0);
        rightInputPanel.addComponent(new EmptySpace());
        rightInputPanel.addComponent(associaVolontariButton);
        rightInputPanel.addComponent(volontariSelezionatiLabel);

        buttonPanel.addComponent(new Button("Aggiungi", () -> tipoVisitaController.aggiungiTipoVisita(
                titoloField.getText(),
                descrizioneField.getText(),
                dataInizioField.getText(),
                dataFineField.getText(),
                oraInizioField.getText(),
                durataField.getText(),
                entrataLibera.getCheckedItem(),
                numeroMinPartecipantiField.getText(),
                numeroMaxPartecipantiField.getText(),
                selezionaLuogoButton.getLabel(),
                volontariSelezionati.toArray(new String[0]), // Passa i volontari selezionati
                giorniSelezionati.toArray(new String[0]),
                indirizzoField.getText(),
                comuneField.getText(),
                provinciaField.getText())));
        buttonPanel.addComponent(fineButton);

        errorPanel.addComponent(errorLabel);
        errorPanel.addComponent(new EmptySpace());

        bottomPanel.addComponent(buttonPanel);

        inputPanel.addComponent(leftInputPanel);
        inputPanel.addComponent(new EmptySpace());
        inputPanel.addComponent(rightInputPanel);

        recapPanel.addComponent(visiteLabel);

        topPanel.addComponent(inputPanel);
        topPanel.addComponent(recapPanel.withBorder(Borders.singleLine()));

        mainPanel.addComponent(topPanel);
        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(bottomPanel);

        window.setComponent(mainPanel);
        window.setHints(List.of(Window.Hint.EXPANDED));
        return window;
    }

    public void mostraErrore(String message) {
        bottomPanel.addComponent(0, errorPanel);
        errorLabel.setText(message);
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
        entrataLibera.setCheckedItemIndex(0); // Resetta alla prima opzione
        volontariSelezionati.clear();
        volontariSelezionatiLabel.setText("Nessun volontario selezionato");
        giorniSelezionati.clear();
        giorniSelezionatiLabel.setText("Nessun giorno selezionato");
        selezionaLuogoButton.setLabel("Nessun luogo selezionato");
        selezionaLuogoButton.setEnabled(true);
        errorLabel.setText("");
        bottomPanel.removeComponent(errorPanel);
    }

    public void mostraVisite(String message) {
        visiteLabel.setText(message);
    }

}
