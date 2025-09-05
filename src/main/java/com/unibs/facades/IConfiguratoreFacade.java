package com.unibs.facades;

import java.time.LocalDate;

public interface IConfiguratoreFacade {

    // --- Metodi per la configurazione ---
    boolean isInitialized();
    boolean regimeAttivo();
    void inizializzaConfigurazioneDefault();
    void setPeriodoCorrente(LocalDate data);
    boolean isCreazioneNuovoPianoPossibile();
    void riapriRaccoltaDisponibilita();

    // --- Metodi di pulizia / rimozione ---
    void applicaRimozioni();
}
