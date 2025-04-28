package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.DatePrecluseService;
import com.unibs.views.AggiungiDatePrecluseView;
import com.googlecode.lanterna.gui2.Button;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;


public class DatePrecluseController {
    private final DatePrecluseService precluseService;
    private final WindowBasedTextGUI gui;
    private final AggiungiDatePrecluseView aggiungiDatePrecluseView;

    public DatePrecluseController(WindowBasedTextGUI gui, DatePrecluseService precluseService) {
        this.precluseService = precluseService;
        this.gui = gui;
        this.aggiungiDatePrecluseView = new AggiungiDatePrecluseView();
    }

    public void apriAggiungiDatePrecluse() {
        LocalDate primaDataPrecludibile = precluseService.getPrimaDataPrecludibile();
        String mese = primaDataPrecludibile.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        mese = mese.substring(0, 1).toUpperCase() + mese.substring(1);
        int anno = primaDataPrecludibile.getYear();
        initDatePrecluseViewListener();
        aggiungiDatePrecluseView.mostra(gui, mese, anno);
    }

    private void initDatePrecluseViewListener() {
        aggiungiDatePrecluseView.getPrecludiButton().addListener(this::aggiungiDataPreclusa);
    }

    private void aggiungiDataPreclusa(Button button) {
        try {
            aggiungiDatePrecluseView.mostraErrore(""); // Pulisce eventuali errori precedenti
            String dataDaPrecludere = aggiungiDatePrecluseView.getData();
            precluseService.aggiungiDataPreclusa(dataDaPrecludere);
            aggiungiDatePrecluseView.mostraSuccesso("Data preclusa con successo!"); // Mostra il messaggio di successo
        } catch (Exception e) {
            aggiungiDatePrecluseView.mostraErrore(e.getMessage());
        } finally {
            aggiungiDatePrecluseView.pulisci();
        }
    }
}
