package com.unibs.controllers;


import java.time.LocalDate;

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
            configController.setInizializedOn(LocalDate.now());
        }
    }

    public boolean checkRegime() {
        return configController.checkRegime();
    }
}
