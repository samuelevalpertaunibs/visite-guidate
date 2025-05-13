package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.DatePrecluseService;
import com.unibs.views.AggiungiDatePrecluseView;
import com.unibs.views.components.PopupChiudi;

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
        aggiungiDatePrecluseView.getPrecludiButton().addListener(button -> aggiungiDataPreclusa());
    }

    private void aggiungiDataPreclusa() {
        try {
            LocalDate dataDaPrecludere = aggiungiDatePrecluseView.getData();
            precluseService.aggiungiDataPreclusa(dataDaPrecludere);
            new PopupChiudi(gui).mostra("", "Data preclusa con successo."); // Mostra il messaggio di successo
            aggiungiDatePrecluseView.mostraErrore("");
        } catch (Exception e) {
            aggiungiDatePrecluseView.mostraErrore(e.getMessage());
        }
    }
}
