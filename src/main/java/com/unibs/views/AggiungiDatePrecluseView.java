package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class AggiungiDatePrecluseView {
    private final Button precludiButton;
    private final Label dataLabel;
    private final Label feedbackLabel;
    private LocalDate dataSelezionata;


    public AggiungiDatePrecluseView() {
        this.dataLabel = new Label("").setPreferredSize(new TerminalSize(10, 1));
        this.feedbackLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.precludiButton = new Button("Precludi");
    }

    private Window creaFinestra() {
        Window window = new BasicWindow("Aggiungi date precluse");
        Panel panel = new Panel();

        dataLabel.setText(dataSelezionata.toString());

        String mese = dataSelezionata.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        int anno = dataSelezionata.getYear();
        panel.addComponent(new Label("Seleziona una data da precludere per " + mese + " " + anno));

        Panel dataInpuPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        dataInpuPanel.addComponent(new Button("-", () -> {
            decrementData();
        }));
        dataInpuPanel.addComponent(dataLabel);
        dataInpuPanel.addComponent(new Button("+", () -> {
            incrementaData();
        }));

        panel.addComponent(dataInpuPanel);
        panel.addComponent(feedbackLabel);
        panel.addComponent(precludiButton);
        Button esciButton = new Button("Esci", window::close);
        panel.addComponent(esciButton);

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
        return window;
    }

    private void decrementData() {
        try {
            aggiornaData(dataSelezionata.withDayOfMonth(dataSelezionata.getDayOfMonth() - 1));
        } catch (Exception e) {}
    }
    private void incrementaData() {
        try {
            aggiornaData(dataSelezionata.withDayOfMonth(dataSelezionata.getDayOfMonth() + 1));
        } catch (Exception e) {}
    }

    private void aggiornaData(LocalDate date) {
        dataSelezionata = date;
        dataLabel.setText(dataSelezionata.toString());
    }

    public void mostraErrore(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setForegroundColor(TextColor.ANSI.RED);
    }

    public void mostraSuccesso(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setForegroundColor(TextColor.ANSI.BLACK);
    }

    public void mostra(WindowBasedTextGUI gui, LocalDate data) {
        this.dataSelezionata = data;
        Window window = creaFinestra();
        gui.addWindowAndWait(window);
    }

    public Button getPrecludiButton() {
        return precludiButton;
    }

    public String getData() {
        return dataLabel.getText();
    }

}
