package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facades.IDatePrecluseFacade;
import com.unibs.views.AggiungiDatePrecluseView;
import com.unibs.views.components.PopupChiudi;

import java.time.LocalDate;


public class DatePrecluseController {
    private final WindowBasedTextGUI gui;
    private final IDatePrecluseFacade datePrecluseFacade;
    private AggiungiDatePrecluseView aggiungiDatePrecluseView;

    public DatePrecluseController(WindowBasedTextGUI gui, IDatePrecluseFacade datePrecluseFacade) {
        this.datePrecluseFacade = datePrecluseFacade;
        this.gui = gui;
    }

    public void apriAggiungiDatePrecluse() {
        aggiungiDatePrecluseView = new AggiungiDatePrecluseView();
        LocalDate primaDataPrecludibile = datePrecluseFacade.getPrimaDataPrecludibile();
        initDatePrecluseViewListener();
        aggiungiDatePrecluseView.mostra(gui, primaDataPrecludibile);
    }

    private void initDatePrecluseViewListener() {
        aggiungiDatePrecluseView.getPrecludiButton().addListener(this::aggiungiDataPreclusa);
    }

    private void aggiungiDataPreclusa(Button button) {
        try {
            LocalDate dataDaPrecludere = aggiungiDatePrecluseView.getData();
            datePrecluseFacade.aggiungiDataPreclusa(dataDaPrecludere);
            new PopupChiudi(gui).mostra("", "Data preclusa con successo."); // Mostra il messaggio di successo
            aggiungiDatePrecluseView.mostraErrore("");
        } catch (Exception e) {
            aggiungiDatePrecluseView.mostraErrore(e.getMessage());
        }
    }
}
