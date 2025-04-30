package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.DatePrecluseService;
import com.unibs.views.AggiungiDatePrecluseView;

import java.time.LocalDate;


public class DatePrecluseController {
    private final DatePrecluseService precluseService;
    private final WindowBasedTextGUI gui;
    private AggiungiDatePrecluseView aggiungiDatePrecluseView;

    public DatePrecluseController(WindowBasedTextGUI gui, DatePrecluseService precluseService) {
        this.precluseService = precluseService;
        this.gui = gui;
    }

    public void apriAggiungiDatePrecluse() {
        aggiungiDatePrecluseView = new AggiungiDatePrecluseView();
        LocalDate primaDataPrecludibile = precluseService.getPrimaDataPrecludibile();
        initDatePrecluseViewListener();
        aggiungiDatePrecluseView.mostra(gui, primaDataPrecludibile);
    }

    private void initDatePrecluseViewListener() {
        aggiungiDatePrecluseView.getPrecludiButton().addListener(this::aggiungiDataPreclusa);
    }

    private void aggiungiDataPreclusa(Button button) {
        try {
            aggiungiDatePrecluseView.mostraErrore(""); // Pulisce eventuali errori precedenti
            LocalDate dataDaPrecludere = aggiungiDatePrecluseView.getData();
            precluseService.aggiungiDataPreclusa(dataDaPrecludere);
            aggiungiDatePrecluseView.mostraSuccesso("Data preclusa con successo!"); // Mostra il messaggio di successo
        } catch (Exception e) {
            aggiungiDatePrecluseView.mostraErrore(e.getMessage());
        }
    }
}
