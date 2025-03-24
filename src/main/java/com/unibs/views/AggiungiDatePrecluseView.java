package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.DatePrecluseController;

public class AggiungiDatePrecluseView {
    private final Button precludeButton;
    private final TextBox dataTextBox;
    private final Label feedbackLabel;


    public AggiungiDatePrecluseView(DatePrecluseController precluseController) {
        this.dataTextBox = new TextBox("");
        this.feedbackLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.precludeButton = new Button("Precludi", () -> precluseController.aggiungiDataPreclusa(dataTextBox.getText()));
    }

    public Window creaFinestra(String mese, int anno) {
        pulisciCampi();
        Window window = new BasicWindow("Aggiungi Data Preclusa");
        Panel panel = new Panel();

        panel.addComponent(new Label("Inserisci un giorno da precludere per " + mese + " " + anno));
        panel.addComponent(dataTextBox);
        panel.addComponent(new EmptySpace());
        panel.addComponent(feedbackLabel);
        panel.addComponent(precludeButton);
        Button esciButton = new Button("Esci", window::close);
        panel.addComponent(esciButton);

        window.setComponent(panel);
        return window;
    }

    private void pulisciCampi() {
        dataTextBox.setText("");
        feedbackLabel.setText("");
    }

    public void mostraErrore(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setForegroundColor(TextColor.ANSI.RED);
    }

    public void mostraSuccesso(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setForegroundColor(TextColor.ANSI.GREEN);
    }
}
