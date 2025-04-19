package com.unibs.views;

import java.util.List;
import java.util.ArrayList;

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

    private final List<String> volontariSelezionati = new ArrayList<>();
    private final List<String> giorniSelezionati = new ArrayList<>();

    public AggiungiTipoVisitaView(TipoVisitaController tipoVisitaController) {
        this.tipoVisitaController = tipoVisitaController;
        this.luogoController = tipoVisitaController.getLuogoController();
        this.volontariController = tipoVisitaController.getVolontariController();
        this.selezionaLuogoView = new SelezionaLuogoView(luogoController);

        titoloField = new TextBox("");
        descrizioneField = new TextBox("");
        indirizzoField = new TextBox("");
        comuneField = new TextBox("");
        provinciaField = new TextBox("");
        dataInizioField = new TextBox("");
        dataFineField = new TextBox("");
        oraInizioField = new TextBox("");
        durataField = new TextBox("");
        entrataLibera = new RadioBoxList<>();
        entrataLibera.addItem("Sì");
        entrataLibera.addItem("No");
        numeroMinPartecipantiField = new TextBox("");
        numeroMaxPartecipantiField = new TextBox("");
        volontariSelezionatiLabel = new Label("Nessun volontario selezionato");
        giorniSelezionatiLabel = new Label("Nessun giorno selezionato");
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        fineButton = new Button("Fine", tipoVisitaController::chiudiFinestraAggiungiTipoVisita);

        this.selezionaLuogoButton = new Button("Nessun luogo selezionato", this::apriPopupSelezioneLuogo);
        this.associaGiorniButton = new Button("Seleziona giorni", this::apriPopupSelezioneGiorni);
        this.associaVolontariButton = new Button("Associa Volontari", this::apriPopupSelezioneVolontari);
    }

    private void apriPopupSelezioneLuogo() {

        selezionaLuogoView.setOnLuogoSelected((luogoSelezionato) -> {
            this.selezionaLuogoButton.setLabel(luogoSelezionato.getNome());
            this.selezionaLuogoButton.setEnabled(false);
        });

        tipoVisitaController.getGui().addWindowAndWait(selezionaLuogoView.creaFinestra());
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
            giorniSelezionatiLabel.setText("Selezionati: " + String.join(", ", giorniSelezionati));
            popupWindow.close();
        });

        panel.addComponent(new EmptySpace());
        panel.addComponent(confermaButton);

        popupWindow.setComponent(panel);
        tipoVisitaController.getGui().addWindowAndWait(popupWindow);
    }

    private void apriPopupSelezioneVolontari() {
        BasicWindow popupWindow = new BasicWindow("Seleziona Volontari");
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
            volontariSelezionatiLabel.setText("Selezionati: " + String.join(", ", volontariSelezionati));
            popupWindow.close();
        });

        panel.addComponent(new EmptySpace());
        panel.addComponent(confermaButton);

        popupWindow.setComponent(panel);
        tipoVisitaController.getGui().addWindowAndWait(popupWindow);
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Aggiungi tipo di visita");
        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        Panel leftPanel = new Panel();
        Panel rightPanel = new Panel();

        leftPanel.addComponent(selezionaLuogoButton);
        leftPanel.addComponent(new Label("Titolo"));
        leftPanel.addComponent(titoloField);

        leftPanel.addComponent(new Label("Descrizione"));
        leftPanel.addComponent(descrizioneField);

        leftPanel.addComponent(new Label("Data inizio (dd/MM)"));
        leftPanel.addComponent(dataInizioField);

        leftPanel.addComponent(new Label("Data fine (dd/MM)"));
        leftPanel.addComponent(dataFineField);

        leftPanel.addComponent(new Label("Ora inizio (HH:mm)"));
        leftPanel.addComponent(oraInizioField);

        leftPanel.addComponent(new Label("Durata (min)"));
        leftPanel.addComponent(durataField);

        Panel puntoIncontroPanel = new Panel();
        puntoIncontroPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        puntoIncontroPanel.addComponent(new Label("Indirizzo"));
        puntoIncontroPanel.addComponent(indirizzoField);
        puntoIncontroPanel.addComponent(new Label("Comune"));
        puntoIncontroPanel.addComponent(comuneField);
        puntoIncontroPanel.addComponent(new Label("Provincia"));
        puntoIncontroPanel.addComponent(provinciaField);

        rightPanel.addComponent(puntoIncontroPanel.withBorder(Borders.singleLine("Punto d'incontro")));

        rightPanel.addComponent(new com.googlecode.lanterna.gui2.EmptySpace());
        rightPanel.addComponent(associaGiorniButton);
        rightPanel.addComponent(giorniSelezionatiLabel);
        rightPanel.addComponent(new com.googlecode.lanterna.gui2.EmptySpace());

        rightPanel.addComponent(new Label("Entrata libera?"));
        rightPanel.addComponent(entrataLibera);

        entrataLibera.setSelectedIndex(0); // TODO: NON FUNZIONA

        rightPanel.addComponent(new Label("Numero minimo partecipanti"));
        rightPanel.addComponent(numeroMinPartecipantiField);

        rightPanel.addComponent(new Label("Numero massimo partecipanti"));
        rightPanel.addComponent(numeroMaxPartecipantiField);

        leftPanel.addComponent(new com.googlecode.lanterna.gui2.EmptySpace());
        rightPanel.addComponent(associaVolontariButton);
        rightPanel.addComponent(volontariSelezionatiLabel);

        mainPanel.addComponent(leftPanel);
        mainPanel.addComponent(rightPanel);
        mainPanel.addComponent(errorLabel);

        mainPanel.addComponent(new EmptySpace());
        mainPanel.addComponent(new Button("Aggiungi", () -> tipoVisitaController.aggiungiTipoVisita(
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
                provinciaField.getText()
        )));

        mainPanel.addComponent(fineButton);

        window.setComponent(mainPanel);
        return window;
    }

    public void mostraErrore(String message) {
        errorLabel.setText(message);
    }

    public void clearAll() {
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

        entrataLibera.setSelectedIndex(0); // Resetta alla prima opzione
        volontariSelezionati.clear();
        volontariSelezionatiLabel.setText("Nessun volontario selezionato");
        giorniSelezionati.clear();
        giorniSelezionatiLabel.setText("Nessun giorno selezionati");

        selezionaLuogoButton.setLabel("Nessun luogo selezionato");
        selezionaLuogoButton.setEnabled(true);

        errorLabel.setText("");
    }

}
