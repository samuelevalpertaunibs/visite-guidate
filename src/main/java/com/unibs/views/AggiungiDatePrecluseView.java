package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.DatePrecluseController;

public class AggiungiDatePrecluseView {
    private final DatePrecluseController precluseController;
    private final Button precludeButton;
    private final TextBox dataTextBox;
    private final Label errorLabel;
    private final Button fineButton;


    public AggiungiDatePrecluseView(DatePrecluseController precluseController) {
        this.precluseController = precluseController;
        this.dataTextBox = new TextBox("");
        this.errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.precludeButton = new Button("Precludi", () -> precluseController.aggiungiDataPreclusa(dataTextBox.getText()));
        this.fineButton = new Button("Fine", precluseController::chiudiAggiungiDatePrecluse);
    }

    public Window creaFinestra(String mese, int anno) {

        Window window = new BasicWindow("Aggiungi Data Preclusa");
        Panel panel = new Panel();

        panel.addComponent(new Label("Inserisci un giorno da precludere per " + mese + " " + anno));
        panel.addComponent(dataTextBox);

        panel.addComponent(errorLabel);

        panel.addComponent(precludeButton);
        panel.addComponent(fineButton);

        window.setComponent(panel);
        return window;
    }

    public void mostraErrore(String message) {
        errorLabel.setText(message);
        errorLabel.setForegroundColor(TextColor.ANSI.RED);
        errorLabel.invalidate();
    }

    public void mostraSuccesso(String message) {
        errorLabel.setText("Successo: " + message);
        errorLabel.setForegroundColor(TextColor.ANSI.GREEN);
        errorLabel.invalidate();  // Forza l'aggiornamento della UI
    }
}
