package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.unibs.controllers.LuogoController;
import com.unibs.controllers.TipoVisitaController;

public class AggiungiTipoVisitaView {
    private final TipoVisitaController tipoVisitaController;
    private final SelezionaLuogoView selezionaLuogoView;
    private Button selezionaLuogoButton;
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

    public AggiungiTipoVisitaView(TipoVisitaController tipoVisitaController, LuogoController luogoController) {
        this.tipoVisitaController = tipoVisitaController;
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
        entrataLibera.setCheckedItemIndex(0);
        numeroMinPartecipantiField = new TextBox("");
        numeroMaxPartecipantiField = new TextBox("");
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);

        // TODO: fix il simbolo di selezione di entrataLibera
        this.selezionaLuogoButton = new Button("Nessun luogo selezionato", () -> {
            // Definisci il callback per gestire la selezione del luogo
            selezionaLuogoView.setOnLuogoSelected((luogoSelezionato) -> {
                // Aggiorna il testo del pulsante con il nome del luogo selezionato
                this.selezionaLuogoButton.setLabel(luogoSelezionato.getNome());
                this.selezionaLuogoButton.setEnabled(false);
                // Quando il luogo viene selezionato fisso il punto di incontro nel comune di
                // appartenenza del luogo
                this.puntoIncontroComuneLabel.setText(" - " + luogoSelezionato.getNomeComune());
            });
            tipoVisitaController.getGui().addWindowAndWait(selezionaLuogoView.creaFinestra());
        });
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
                    selezionaLuogoButton.getLabel());
        }));

        Button confirmButton = new Button("Mostra Conferma", () -> {
            MessageDialogButton result = MessageDialog.showMessageDialog(
                    tipoVisitaController.getGui(),
                    "Conferma",
                    "Sei sicuro di procedere?",
                    MessageDialogButton.Yes,
                    MessageDialogButton.No);

            // Azione in base alla risposta
            if (result == MessageDialogButton.Yes) {
                MessageDialog.showMessageDialog(tipoVisitaController.getGui(), "Risultato", "Hai scelto Sì!");
            } else {
                MessageDialog.showMessageDialog(tipoVisitaController.getGui(), "Risultato", "Hai scelto No!");
            }
        });

        panel.addComponent(confirmButton);

        window.setComponent(panel);
        return window;
    }

    public void mostraErrore(String message) {
        errorLabel.setText(message);
    }
}
