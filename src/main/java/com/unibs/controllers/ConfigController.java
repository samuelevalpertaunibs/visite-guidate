package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.ConfigService;
import com.unibs.models.Comune;
import com.unibs.views.InitConfigView;
import com.unibs.views.ModificaNumeroMaxView;
import com.unibs.views.RegimeNonAttivoView;

import java.time.LocalDate;
import java.util.List;

public class ConfigController {
    private final ConfigService configService;
    private final WindowBasedTextGUI gui;
    private final InitConfigView initConfigView;
    private final ModificaNumeroMaxView modificaNumeroMaxView;
    private final RegimeNonAttivoView regimeNonAttivoView;

    public ConfigController(WindowBasedTextGUI gui) {
        this.configService = new ConfigService();
        this.initConfigView = new InitConfigView(this);
        this.modificaNumeroMaxView = new ModificaNumeroMaxView(this);
        this.gui = gui;
        this.regimeNonAttivoView = new RegimeNonAttivoView();
    }

    public void apriConfigurazione() {
        gui.addWindowAndWait(initConfigView.creaFinestra());
    }

    public void aggiungiComune(String nome, String provincia, String regione) {
        Comune comuneDaAggiungere = new Comune(0, nome, provincia, regione);
        try {
            configService.aggiungiComune(comuneDaAggiungere);
            StringBuilder sb = new StringBuilder();
            sb.append("Ambito territoriale:\n");
            List<Comune> ambitoTerritoriale = configService.getAmbitoTerritoriale();
            for (Comune comune : ambitoTerritoriale) {
                sb.append(" - ").append(comune.toString()).append("\n");
            }
            initConfigView.mostraAmbito(sb.toString());
            initConfigView.resetComune();
            initConfigView.moveCursorToProsegui();
        } catch (Exception e) {
            initConfigView.mostraErroreComune(e.getMessage());
            initConfigView.moveCursorToComune();
        }
    }

    public void conferma(String numeroMaxPersone) {
        try {
            configService.setNumeroMaxPersone(numeroMaxPersone);
            gui.removeWindow(gui.getActiveWindow());
        } catch (Exception e) {
            initConfigView.mostraErroreNumeroMax(e.getMessage());
            initConfigView.resetNumeroMax();
            initConfigView.moveCursorToNumeroMax();
        }
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

    public void confermaAmbito() {
        try {
            if (!configService.esisteAlmenoUnComune()) {
                initConfigView.mostraErroreComune("Inserisci almeno un comune");
                initConfigView.moveCursorToComune();
                return;
            }
            initConfigView.mostraNumeroMaxPanel();
        } catch (Exception e) {
            initConfigView.mostraErroreComune(e.getMessage());
        }
    }
}
