package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.DatePrecluseService;
import com.unibs.views.AggiungiDatePrecluseView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;


public class DatePrecluseController {
    private final DatePrecluseService precluseService;
    private final WindowBasedTextGUI gui;
    private final AggiungiDatePrecluseView aggiungiDatePrecluseView;

    protected DatePrecluseController(WindowBasedTextGUI gui) {
        this.precluseService = new DatePrecluseService();
        this.gui = gui;
        this.aggiungiDatePrecluseView = new AggiungiDatePrecluseView(this);
    }

    public void apriAggiungiDatePrecluse() {
        LocalDate primaDataPrecludibile = precluseService.getPrimaDataPrecludibile();
        String mese = primaDataPrecludibile.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        mese = mese.substring(0, 1).toUpperCase() + mese.substring(1);
        int anno = primaDataPrecludibile.getYear();
        gui.addWindowAndWait(aggiungiDatePrecluseView.creaFinestra(mese, anno));
    }

    public void aggiungiDataPreclusa(String data) {
        try {
            aggiungiDatePrecluseView.mostraErrore(""); // Pulisce eventuali errori precedenti
            precluseService.aggiungiDataPrecluse(data);
            aggiungiDatePrecluseView.mostraSuccesso("Data aggiunta con successo!"); // Mostra il messaggio di successo
        } catch (Exception e) {
            aggiungiDatePrecluseView.mostraErrore(e.getMessage());
        }
    }
}
