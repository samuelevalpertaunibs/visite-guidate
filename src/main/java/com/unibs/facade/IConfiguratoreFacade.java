package com.unibs.facade;

import java.time.LocalDate;

public interface IConfiguratoreFacade {

    // --- Metodi per la configurazione ---
    boolean isInitialized();
    boolean regimeAttivo();
    void initDefault();
    void setPeriodoCorrente(LocalDate data);
    boolean isCreazioneNuovoPianoPossibile();
    void riapriRaccoltaDisponibilita();

    // --- Metodi di pulizia / rimozione ---
    void applicaRimozioni();
}
