package com.unibs.facade;

import com.unibs.services.*;
import java.time.LocalDate;

public class ConfiguratoreFacade implements IConfiguratoreFacade {

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
    @Override
    public boolean isInitialized() {
        return configService.isInitialized();
    }

    @Override
    public boolean regimeAttivo() {
        return configService.regimeAttivo();
    }

    @Override
    public void initDefault() {
        configService.initDefault();
    }

    @Override
    public void setPeriodoCorrente(LocalDate data) {
        configService.setPeriodoCorrente(data);
    }

    @Override
    public boolean isCreazioneNuovoPianoPossibile() {
        return configService.isCreazioneNuovoPianoPossibile();
    }

    @Override
    public void riapriRaccoltaDisponibilita() {
        configService.riapriRaccoltaDisponibilita();
    }

    // --- Metodi di pulizia / rimozione ---
    @Override
    public void applicaRimozioni() {
        luogoService.applicaRimozioneLuoghi();
        tipoVisitaService.applicaRimozioneTipiVisita();
        volontarioService.applicaRimozioneVolontari();
        luogoService.rimuoviNonAssociati();
        tipoVisitaService.rimuoviNonAssociati();
        volontarioService.rimuoviNonAssociati();
    }
}
