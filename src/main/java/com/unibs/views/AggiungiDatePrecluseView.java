package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.DatePrecluseController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class AggiungiDatePrecluseView {
    private final DatePrecluseController precluseController;
    private Button precluseButton;
    private final TextBox dataTextBox;
    private final Label errorLabel;


    public AggiungiDatePrecluseView(DatePrecluseController precluseController) {
        this.precluseController = precluseController;

        this.dataTextBox = new TextBox("");
        errorLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
    }

    public Window creaFinestra() {
        LocalDateTime now = LocalDateTime.now().plusMonths(3);
        String mese = now.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        int anno = now.getYear();
        Window window = new BasicWindow("Aggiungi Data Preclusa");
        Panel panel = new Panel();

        panel.addComponent(new Label("Inserisci il giorno da precludere per " + mese + " " + anno));
        panel.addComponent(dataTextBox);

        panel.addComponent(errorLabel);

        panel.addComponent(new Button("Aggiungi", () -> {
            precluseController.aggiungiDataPrecluse(dataTextBox.getText());
        }));

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
