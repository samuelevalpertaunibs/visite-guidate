package com.unibs.facades;

import com.unibs.models.Config;

public interface IConfigFacade {

    void aggiungiComune(String nome, String provincia, String regione) throws Exception;

    int setNumeroMaxPersone(String numeroMax) throws Exception;

    int getNumeroMax() throws Exception;

    Config getConfig() throws Exception;

    boolean esisteAlmenoUnComune() throws Exception;
}
