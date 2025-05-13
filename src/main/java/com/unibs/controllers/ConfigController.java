package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.Comune;
import com.unibs.models.Config;
import com.unibs.services.ConfigService;
import com.unibs.views.InitConfigView;
import com.unibs.views.ModificaNumeroMaxView;
import com.unibs.views.components.PopupChiudi;

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
        initConfigView.getAggiungiComuneButton().addListener(button -> aggiungiComune());
        initConfigView.getConfermaButton().addListener(button1 -> confermaConfig());
        initConfigView.getProseguiButton().addListener(button -> confermaAmbito());
    }

    private void aggiungiComune() {
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

    private void confermaConfig() {
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
        try {
            modificaNumeroMaxView = new ModificaNumeroMaxView();
            int numeroMaxAttuale = configService.getNumeroMax();
            modificaNumeroMaxView.aggiornaNumeroAttuale(numeroMaxAttuale);
            modificaNumeroMaxView.getConfermaButton().addListener(button -> setNumeroMaxPersone());
            modificaNumeroMaxView.mostra(gui);
        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
    }

    private void setNumeroMaxPersone() {
        try {
            String numeroMaxPersone = modificaNumeroMaxView.getNumeroMaxInserito();
            int numeroMaxAggiornato = configService.setNumeroMaxPersone(numeroMaxPersone);
            modificaNumeroMaxView.aggiornaNumeroAttuale(numeroMaxAggiornato);
            new PopupChiudi(gui).mostra("", "Numero massimo aggiornato con successo.");
            modificaNumeroMaxView.mostraErrore("");
        } catch (Exception e) {
            modificaNumeroMaxView.mostraErrore(e.getMessage());
        } finally {
            modificaNumeroMaxView.pulisci();
        }
    }

    private void confermaAmbito() {
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
