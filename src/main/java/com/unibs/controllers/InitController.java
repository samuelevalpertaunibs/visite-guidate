package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

public class InitController {
    private final ConfigController configController;
    private final TipoVisitaController tipoVisitaController;

    public InitController(WindowBasedTextGUI gui, ConfigController configController) {
        this.configController = configController;
        this.tipoVisitaController = new TipoVisitaController(gui);
    }

    public void assertInizializzazione() {
        if (!configController.isInitialized()) {
            configController.initDefault();
            configController.apriConfigurazione();
            tipoVisitaController.apriAggiungiTipoVisita();
            configController.setIsInizialed(true);
        }
    }
}
