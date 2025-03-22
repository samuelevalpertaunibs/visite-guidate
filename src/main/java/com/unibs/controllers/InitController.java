package com.unibs.controllers;


public class InitController {
    private final ConfigController configController;
    private final TipoVisitaController tipoVisitaController;

    public InitController(ConfigController configController, TipoVisitaController tipoVisitaController) {
        this.configController = configController;
        this.tipoVisitaController = tipoVisitaController;
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
