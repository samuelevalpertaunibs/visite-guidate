package com.unibs.facade;

import com.unibs.models.Config;
import com.unibs.models.Comune;

import java.util.List;

public interface IConfigFacade {

    void aggiungiComune(String nome, String provincia, String regione) throws Exception;

    int setNumeroMaxPersone(String numeroMax) throws Exception;

    int getNumeroMax() throws Exception;

    Config getConfig() throws Exception;

    boolean esisteAlmenoUnComune() throws Exception;
}
