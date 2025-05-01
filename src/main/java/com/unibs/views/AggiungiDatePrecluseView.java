package com.unibs.views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class AggiungiDatePrecluseView {
    private final Button precludiButton;
    private final Label dataLabel;
    private final Label erroreLabel;
    private LocalDate dataSelezionata;


    public AggiungiDatePrecluseView() {
        this.dataLabel = new Label("").setPreferredSize(new TerminalSize(10, 1));
        this.erroreLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        this.precludiButton = new Button("Precludi");
    }

    private Window creaFinestra() {
        Window window = new BasicWindow("Aggiungi date precluse");
        Panel panel = new Panel();

        aggiornaData(dataSelezionata);

        String mese = dataSelezionata.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        int anno = dataSelezionata.getYear();
        panel.addComponent(new Label("Seleziona una data da precludere per " + mese + " " + anno));

        Panel dataInpuPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));
        dataInpuPanel.addComponent(new Button("-", this::decrementData).setPreferredSize(new TerminalSize(5, 1)));
        dataInpuPanel.addComponent(dataLabel);
        dataInpuPanel.addComponent(new Button("+", this::incrementaData).setPreferredSize(new TerminalSize(5, 1)));

        panel.addComponent(dataInpuPanel);
        panel.addComponent(erroreLabel);
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
        } catch (Exception ignored) {}
    }
    private void incrementaData() {
        try {
            aggiornaData(dataSelezionata.withDayOfMonth(dataSelezionata.getDayOfMonth() + 1));
        } catch (Exception ignored) {}
    }

    private void aggiornaData(LocalDate date) {
        dataSelezionata = date;
        dataLabel.setText(dataSelezionata.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public void mostraErrore(String message) {
        erroreLabel.setText(message);

    }

    public void mostra(WindowBasedTextGUI gui, LocalDate data) {
        this.dataSelezionata = data;
        Window window = creaFinestra();
        gui.addWindowAndWait(window);
    }

    public Button getPrecludiButton() {
        return precludiButton;
    }

    public LocalDate getData() {
        return dataSelezionata;
    }

}
