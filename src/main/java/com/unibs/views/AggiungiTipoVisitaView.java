package com.unibs.views;

import java.util.List;
import java.util.ArrayList;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.unibs.controllers.LuogoController;
import com.unibs.controllers.TipoVisitaController;
import com.unibs.controllers.VolontariController;

public class AggiungiTipoVisitaView {
    private final TipoVisitaController tipoVisitaController;
    private final VolontariController volontariController;
    private final SelezionaLuogoView selezionaLuogoView;

    private Button selezionaLuogoButton;
    private Button associaVolontariButton;

    private final TextBox titoloField;
    private final TextBox descrizioneField;
    private final TextBox dataInizioField;
    private final TextBox dataFineField;
    private final TextBox oraInizioField;
    private final TextBox durataField;
    private final TextBox puntoIncontroField;
    private final RadioBoxList<String> entrataLibera;
    private final TextBox numeroMinPartecipantiField;
    private final TextBox numeroMaxPartecipantiField;
    private final Label puntoIncontroComuneLabel;
    private final Label errorLabel;
    private final Label volontariSelezionatiLabel;

    private List<String> volontariSelezionati = new ArrayList<>();

    public AggiungiTipoVisitaView(TipoVisitaController tipoVisitaController, LuogoController luogoController) {
        this.tipoVisitaController = tipoVisitaController;
        this.volontariController = new VolontariController();
        this.selezionaLuogoView = new SelezionaLuogoView(luogoController);

        titoloField = new TextBox("");
        descrizioneField = new TextBox("");
        puntoIncontroField = new TextBox("");
        puntoIncontroComuneLabel = new Label("");
        dataInizioField = new TextBox("");
        dataFineField = new TextBox("");
        oraInizioField = new TextBox("");
        durataField = new TextBox("");
        entrataLibera = new RadioBoxList<>();
        entrataLibera.addItem("Sì");
        entrataLibera.addItem("No");
        entrataLibera.setSelectedIndex(0);
        numeroMinPartecipantiField = new TextBox("");
        numeroMaxPartecipantiField = new TextBox("");
        volontariSelezionatiLabel = new Label("Nessun volontario selezionato");
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);

        this.selezionaLuogoButton = new Button("Nessun luogo selezionato", () -> {
            selezionaLuogoView.setOnLuogoSelected((luogoSelezionato) -> {
                this.selezionaLuogoButton.setLabel(luogoSelezionato.getNome());
                this.selezionaLuogoButton.setEnabled(false);
                this.puntoIncontroComuneLabel.setText(" - " + luogoSelezionato.getNomeComune());
            });
            tipoVisitaController.getGui().addWindowAndWait(selezionaLuogoView.creaFinestra());
        });

        this.associaVolontariButton = new Button("Associa Volontari", () -> apriPopupSelezioneVolontari());
    }

    private void apriPopupSelezioneVolontari() {
        BasicWindow popupWindow = new BasicWindow("Seleziona Volontari");
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        List<CheckBox> checkBoxList = new ArrayList<>();
        List<String> volontariDisponibili = volontariController.getListaVolontari(); // Ottiene la lista dal controller

        for (String volontario : volontariDisponibili) {
            CheckBox checkBox = new CheckBox(volontario);
            if (volontariSelezionati.contains(volontario))
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
        Panel panel = new Panel();
        panel.addComponent(selezionaLuogoButton);

        panel.addComponent(new Label("Titolo"));
        panel.addComponent(titoloField);

        panel.addComponent(new Label("Descrizione"));
        panel.addComponent(descrizioneField);

        Panel puntoIncontroPanel = new Panel();
        puntoIncontroPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        puntoIncontroPanel.addComponent(puntoIncontroField);

        puntoIncontroPanel.addComponent(puntoIncontroComuneLabel);
        panel.addComponent(new Label("Punto d'incontro"));
        panel.addComponent(puntoIncontroPanel);

        panel.addComponent(new Label("Data inizio (dd/MM)"));
        panel.addComponent(dataInizioField);

        panel.addComponent(new Label("Data fine (dd/MM)"));
        panel.addComponent(dataFineField);

        panel.addComponent(new Label("Ora inizio (HH:mm)"));
        panel.addComponent(oraInizioField);

        panel.addComponent(new Label("Durata (min)"));
        panel.addComponent(durataField);

        panel.addComponent(new Label("Entrata libera?"));
        panel.addComponent(entrataLibera);

        panel.addComponent(new Label("Numero minimo partecipanti"));
        panel.addComponent(numeroMinPartecipantiField);

        panel.addComponent(new Label("Numero massimo partecipanti"));
        panel.addComponent(numeroMaxPartecipantiField);

        panel.addComponent(new com.googlecode.lanterna.gui2.EmptySpace());
        panel.addComponent(associaVolontariButton);
        panel.addComponent(volontariSelezionatiLabel);

        panel.addComponent(errorLabel);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Aggiungi", () -> {
            tipoVisitaController.aggiungiTipoVisita(
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
                    volontariSelezionati.toArray(new String[0]) // Passa i volontari selezionati
            );
        }));

        window.setComponent(panel);
        return window;
    }

    public void mostraErrore(String message) {
        errorLabel.setText(message);
    }
}
