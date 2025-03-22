package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.DatePrecluseService;
import com.unibs.views.AggiungiDatePrecluseView;


public class DatePrecluseController {
    private final DatePrecluseService precluseService;
    private final WindowBasedTextGUI gui;
    private final AggiungiDatePrecluseView aggiungiDatePrecluseView;

    protected DatePrecluseController(WindowBasedTextGUI gui) {
        this.precluseService = new DatePrecluseService();
        this.gui = gui;
        this.aggiungiDatePrecluseView = new AggiungiDatePrecluseView(this);
    }

    public void apriAggiungiDatePrecluse() {gui.addWindowAndWait(aggiungiDatePrecluseView.creaFinestra());};

    public void aggiungiDataPrecluse(String data) {
        try {
            aggiungiDatePrecluseView.mostraErrore(""); // Pulisce eventuali errori precedenti
            precluseService.aggiungiDataPrecluse(data);
            aggiungiDatePrecluseView.mostraSuccesso("Data aggiunta con successo!"); // Mostra il messaggio di successo
        } catch (Exception e) {
            aggiungiDatePrecluseView.mostraErrore(e.getMessage());
        }
    }

    public WindowBasedTextGUI getGui() {
        return this.gui;
    }

}
