package com.unibs.facades;

import com.unibs.services.*;

import java.time.LocalDate;

public class ConfiguratoreFacade {

    private final ConfigService configService;
    private final LuogoService luogoService;
    private final TipoVisitaService tipoVisitaService;
    private final VolontarioService volontarioService;

    public ConfiguratoreFacade(ServiceFactory serviceFactory) {
        this.configService = serviceFactory.getConfigService();
        this.luogoService = serviceFactory.getLuogoService();
        this.tipoVisitaService = serviceFactory.getTipoVisitaService();
        this.volontarioService = serviceFactory.getVolontarioService();
    }

    // --- Metodi per la configurazione ---
    public boolean isInitialized() {
        return configService.isInitialized();
    }

    public boolean regimeAttivo() {
        return configService.regimeAttivo();
    }

    public void inizializzaConfigurazioneDefault() {
        configService.initDefault();
    }

    public void setPeriodoCorrente(LocalDate data) {
        configService.setPeriodoCorrente(data);
    }

    public boolean isCreazioneNuovoPianoPossibile() {
        return configService.isCreazioneNuovoPianoPossibile();
    }

    public void riapriRaccoltaDisponibilita() {
        configService.riapriRaccoltaDisponibilita();
    }

    // --- Metodi di pulizia / rimozione ---
    public void applicaRimozioni() {
        luogoService.applicaRimozioneLuoghi();
        tipoVisitaService.applicaRimozioneTipiVisita();
        volontarioService.applicaRimozioneVolontari();
        luogoService.rimuoviNonAssociati();
        tipoVisitaService.rimuoviNonAssociati();
        volontarioService.rimuoviNonAssociati();
    }

}
