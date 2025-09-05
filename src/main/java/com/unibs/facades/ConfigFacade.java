package com.unibs.facades;

import com.unibs.models.Config;
import com.unibs.services.ConfigService;
import com.unibs.services.ServiceFactory;

public class ConfigFacade implements IConfigFacade {

    private final ConfigService configService;

    public ConfigFacade(ServiceFactory serviceFactory) {
        this.configService = serviceFactory.getConfigService();
    }

    @Override
    public void aggiungiComune(String nome, String provincia, String regione) throws Exception {
        configService.aggiungiComune(nome, provincia, regione);
    }

    @Override
    public int setNumeroMaxPersone(String numeroMax) throws Exception {
        return configService.setNumeroMaxPersone(numeroMax);
    }

    @Override
    public int getNumeroMax() throws Exception {
        return configService.getNumeroMax();
    }

    @Override
    public Config getConfig() throws Exception {
        return configService.getConfig();
    }

    @Override
    public boolean esisteAlmenoUnComune() throws Exception {
        return configService.esisteAlmenoUnComune();
    }
}
