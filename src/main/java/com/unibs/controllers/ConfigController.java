package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.daos.ConfigDao;
import com.unibs.services.ConfigService;
import com.unibs.models.Comune;
import com.unibs.models.Config;
import com.unibs.views.ConfigView;

import java.util.ArrayList;

public class ConfigController {
    private final ConfigService configService;
    private final WindowBasedTextGUI gui;
    private final ConfigView view;

    protected ConfigController(WindowBasedTextGUI gui) {
        this.configService = new ConfigService();
        this.view = new ConfigView(this);
        this.gui = gui;
    }

    public Window getView() {
        return this.view.creaFinestra();
    }

    public void setIsInitialized(boolean isInitialized) {
        ConfigDao.setIsInitialized(isInitialized);
    }

    public void aggiungiComune(String nome, String provincia, String regione) {
        Comune comune = new Comune(nome, provincia, regione);
        try {
            configService.aggiungiComune(comune);
            view.showPopupMessage("Il comune è stato aggiunto correttamente.");
            view.resetComune();
            view.moveCursorToNumeroMax();
        } catch (Exception e) {
            view.showComuneErrorMessage(e.getMessage());
            view.moveCursorToComune();
        }
    }

    public void conferma(String numeroMaxPersone) {
        if (!configService.esisteAlmenoUnComune()) {
            view.showPopupMessage("Inserisci almeno un comune.");
            view.moveCursorToComune();
            return;
        }
        try {
            configService.setNumeroMaxPersone(numeroMaxPersone);
            Config config = configService.getConfig();
            StringBuilder sb = new StringBuilder();
            sb.append("Ambito territoriale:\n");
            for (Comune comune : config.getAmbitoTerritoriale()) {
                sb.append(" - " + comune.toString() + "\n");
            }
            sb.append(String.format("\nNumero max persone: %d\n", config.getNumeroMassimoIscrizioniPrenotazione()));
            view.showConfirmMessage(sb.toString());
        } catch (Exception e) {
            view.showNumeroMaxErrorMessage(e.getMessage());
            view.resetNumeroMax();
            view.moveCursorToNumeroMax();
        }
    }

    public WindowBasedTextGUI getGui() {
        return this.gui;
    }

    public void nonConferma() {
        //configService.initDefault();
        view.resetNumeroMax();
        view.resetComune();
        view.resetError();
        view.moveCursorToComune();
    }

    public void haConfermato() {
        gui.removeWindow(gui.getActiveWindow());
    }

    public ArrayList<Comune> getAmbitoTerritoriale() {
        return configService.getAmbitoTerritoriale();
    }
}
