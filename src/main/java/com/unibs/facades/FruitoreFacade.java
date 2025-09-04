package com.unibs.facades;

import com.unibs.models.Fruitore;
import com.unibs.models.Visita;
import com.unibs.services.ConfigService;
import com.unibs.services.FruitoreService;
import com.unibs.services.ServiceFactory;
import com.unibs.services.VisitaService;

import java.util.List;

public class FruitoreFacade {

    private final ConfigService configService;
    private final VisitaService visitaService;
    private final FruitoreService fruitoreService;

    public FruitoreFacade(ServiceFactory serviceFactory) {
        this.configService = serviceFactory.getConfigService();
        this.visitaService = serviceFactory.getVisitaService();
        this.fruitoreService = serviceFactory.getFruitoreService();
    }

    // --- Config ---
    public boolean isRegimeAttivo() {
        return configService.regimeAttivo();
    }

    // --- Visite ---
    public List<Visita> getVisiteProposte() {
        return visitaService.getVisitePreviewByStato(Visita.StatoVisita.PROPOSTA);
    }

    public void disdiciVisita(Fruitore fruitore, int codiceIscrizione) {
        visitaService.disdici(fruitore, codiceIscrizione);
    }

    // --- Iscrizioni ---
    public int iscriviAdUnaVisita(Fruitore fruitore, Visita visita, int numeroIscritti) {
        return fruitoreService.iscrivi(fruitore, visita, numeroIscritti);
    }
}
