package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.Comune;
import com.unibs.models.Config;
import com.unibs.services.ConfigService;
import com.unibs.views.InitConfigView;
import com.unibs.views.ModificaNumeroMaxView;

public class ConfigController {
    private final ConfigService configService;
    private final WindowBasedTextGUI gui;
    private InitConfigView initConfigView;
    private ModificaNumeroMaxView modificaNumeroMaxView;

    public ConfigController(WindowBasedTextGUI gui, ConfigService configService) {
        this.configService = configService;
        this.gui = gui;
    }

    public void apriConfigurazione() {
        initConfigView = new InitConfigView();
        initListenerInitConfigView();
        initConfigView.mostra(gui);
    }

    private void initListenerInitConfigView() {
        initConfigView.getAggiungiComuneButton().addListener(this::aggiungiComune);
        initConfigView.getConfermaButton().addListener(this::confermaConfig);
        initConfigView.getProseguiButton().addListener(this::confermaAmbito);
    }

    private void aggiungiComune(Button button) {
        String nome = initConfigView.getNome();
        String provincia = initConfigView.getProvincia();
        String regione = initConfigView.getRegione();
        try {
            configService.aggiungiComune(nome, provincia, regione);
            String ambitoTerritorialeRecap = generaAmbitoTerritorialeRecap();
            initConfigView.mostraAmbito(ambitoTerritorialeRecap);
            initConfigView.resetComune();
            initConfigView.moveCursorToProsegui();
        } catch (Exception e) {
            initConfigView.mostraErroreComune(e.getMessage());
        }
    }

    private void confermaConfig(Button button) {
        try {
            String numeroMaxPersone = initConfigView.getNumeroMax();
            configService.setNumeroMaxPersone(numeroMaxPersone);
            initConfigView.chiudi();
        } catch (Exception e) {
            initConfigView.mostraErroreNumeroMax(e.getMessage());
            initConfigView.resetNumeroMax();
            initConfigView.moveCursorToNumeroMax();
        }
    }

    private String generaAmbitoTerritorialeRecap() {
        try {
            Config config = configService.getConfig();
            StringBuilder sb = new StringBuilder();
            sb.append("Ambito territoriale:\n");
            for (Comune comune : config.getAmbitoTerritoriale()) {
                sb.append(" - ").append(comune.toString()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            initConfigView.mostraErroreComune(e.getMessage());
        }
        return null;
    }

    public void apriModificaNumeroMax() {
        modificaNumeroMaxView = new ModificaNumeroMaxView();
        int numeroMaxAttuale = configService.getNumeroMax();
        modificaNumeroMaxView.aggiornaNumeroAttuale(numeroMaxAttuale);
        modificaNumeroMaxView.getConfermaButton().addListener(this::setNumeroMaxPersone);
        modificaNumeroMaxView.mostra(gui);
    }

    private void setNumeroMaxPersone(Button button) {
        try {
            String numeroMaxPersone = modificaNumeroMaxView.getNumeroMaxInserito();
            int numeroMaxAggiornato = configService.setNumeroMaxPersone(numeroMaxPersone);
            modificaNumeroMaxView.mostraSuccesso("Numero massimo aggiornato con successo.");
            modificaNumeroMaxView.aggiornaNumeroAttuale(numeroMaxAggiornato);
        } catch (Exception e) {
            modificaNumeroMaxView.mostraErrore(e.getMessage());
        } finally {
            modificaNumeroMaxView.pulisci();
        }
    }

    private void confermaAmbito(Button button) {
        try {
            if (!configService.esisteAlmenoUnComune()) {
                initConfigView.mostraErroreComune("Inserisci almeno un comune.");
                initConfigView.moveCursorToComune();
                return;
            }
            initConfigView.mostraNumeroMaxPanel();
        } catch (Exception e) {
            initConfigView.mostraErroreComune(e.getMessage());
        }
    }
}
