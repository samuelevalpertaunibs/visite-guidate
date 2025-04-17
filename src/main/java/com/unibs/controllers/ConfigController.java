package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.ConfigService;
import com.unibs.models.Comune;
import com.unibs.models.Config;
import com.unibs.views.InitConfigView;
import com.unibs.views.ModificaNumeroMaxView;
import com.unibs.views.RegimeNonAttivo;

import java.time.LocalDate;
import java.util.List;

public class ConfigController {
    private final ConfigService configService;
    private final WindowBasedTextGUI gui;
    private final InitConfigView initConfigView;
    private final ModificaNumeroMaxView modificaNumeroMaxView;
    private final RegimeNonAttivo regimeNonAttivoView;

    public ConfigController(WindowBasedTextGUI gui) {
        this.configService = new ConfigService();
        this.initConfigView = new InitConfigView(this);
        this.modificaNumeroMaxView = new ModificaNumeroMaxView(this);
        this.gui = gui;
        this.regimeNonAttivoView = new RegimeNonAttivo();
    }

    public void apriConfigurazione() {
        gui.addWindowAndWait(initConfigView.creaFinestra());
    }

    public void aggiungiComune(String nome, String provincia, String regione) {
        Comune comune = new Comune(0, nome, provincia, regione);
        try {
            configService.aggiungiComune(comune);
            initConfigView.showPopupMessage("Comune aggiunto", "Il comune è stato aggiunto correttamente.");
            initConfigView.resetComune();
            initConfigView.moveCursorToNumeroMax();
        } catch (Exception e) {
            initConfigView.showComuneErrorMessage(e.getMessage());
            initConfigView.moveCursorToComune();
        }
    }

    public void conferma(String numeroMaxPersone) {
        if (!configService.esisteAlmenoUnComune()) {
            initConfigView.showPopupMessage("Errore", "Inserisci almeno un comune.");
            initConfigView.moveCursorToComune();
            return;
        }
        try {
            configService.setNumeroMaxPersone(numeroMaxPersone);
            Config config = configService.getConfig();
            StringBuilder sb = new StringBuilder();
            sb.append("Ambito territoriale:\n");
            for (Comune comune : config.getAmbitoTerritoriale()) {
                sb.append(" - ").append(comune.toString()).append("\n");
            }
            sb.append(String.format("\nNumero max persone: %d\n", config.getNumeroMassimoIscrizioniPrenotazione()));
            initConfigView.showConfirmMessage(sb.toString());
        } catch (Exception e) {
            initConfigView.showNumeroMaxErrorMessage(e.getMessage());
            initConfigView.resetNumeroMax();
            initConfigView.moveCursorToNumeroMax();
        }
    }

    public void nonConferma() {
        initConfigView.resetNumeroMax();
        initConfigView.resetComune();
        initConfigView.resetError();
        initConfigView.moveCursorToComune();
    }

    public void haConfermato() {
        gui.removeWindow(gui.getActiveWindow());
    }

    public void initDefault() {
        configService.initDefault();
    }

    public boolean isInitialized() {
        return configService.isInitialized();
    }

    public void setInizializedOn(LocalDate date) {
        configService.setInitializedOn(date);
    }

    public void apriModificaNumeroMax() {
        int numeroMaxAttuale = getNumeroMax();
        gui.addWindowAndWait(modificaNumeroMaxView.creaFinestra(numeroMaxAttuale));
    }

    public int getNumeroMax() {
        return configService.getNumeroMaxPersone();
    }

    public List<Comune> getAmbitoTerritoriale() {
        return configService.getAmbitoTerritoriale();
    }

    public WindowBasedTextGUI getGui() {
        return gui;
    }

    public void setNumeroMaxPersone(String numeroMaxPersone) {
        try {
            configService.setNumeroMaxPersone(numeroMaxPersone);
            modificaNumeroMaxView.mostraSuccesso("Numero massimo aggiornato con successo!");
            modificaNumeroMaxView.aggiornaNumeroAttuale(numeroMaxPersone);
        }  catch (Exception e) {
            modificaNumeroMaxView.mostraErrore(e.getMessage());
        }
    }

    public boolean checkRegime() {
        if (!configService.regimeAttivo()) {
            gui.addWindowAndWait(regimeNonAttivoView.creaFinestra());
            return false;
        }
        return true;
    }
}
