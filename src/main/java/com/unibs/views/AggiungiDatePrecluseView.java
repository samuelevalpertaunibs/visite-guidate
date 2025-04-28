package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

public class AggiungiDatePrecluseView {
    private final Button precludiButton;
    private final TextBox dataTextBox;
    private final Label feedbackLabel;


    public AggiungiDatePrecluseView() {
        this.dataTextBox = new TextBox("");
        this.feedbackLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.precludiButton = new Button("Precludi");
    }

    private Window creaFinestra(String mese, int anno) {
        pulisciCampi();
        Window window = new BasicWindow("Aggiungi date precluse");
        Panel panel = new Panel();

        panel.addComponent(new Label("Inserisci un giorno da precludere per " + mese + " " + anno));
        panel.addComponent(dataTextBox);
        panel.addComponent(feedbackLabel);
        panel.addComponent(precludiButton);
        Button esciButton = new Button("Esci", window::close);
        panel.addComponent(esciButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
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
        feedbackLabel.setForegroundColor(TextColor.ANSI.BLACK);
    }

    public void mostra(WindowBasedTextGUI gui, String mese, int anno) {
        Window window = creaFinestra(mese, anno);
        gui.addWindowAndWait(window);
    }

    public Button getPrecludiButton() {
        return precludiButton;
    }

    public String getData() {
        return dataTextBox.getText();
    }

    public void pulisci() {
        dataTextBox.setText("");
        dataTextBox.takeFocus();
    }
}
