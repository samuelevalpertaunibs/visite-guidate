package com.unibs.facade;

import com.unibs.models.Visita;
import com.unibs.services.ConfigService;
import com.unibs.services.ServiceFactory;
import com.unibs.services.VisitaService;

import java.util.List;

public class VisitaFacade implements IVisitaFacade {

    private final VisitaService visitaService;
    private final ConfigService configService;

    public VisitaFacade(ServiceFactory serviceFactory) {
        this.visitaService = serviceFactory.getVisitaService();
        this.configService = serviceFactory.getConfigService();
    }

    // --- Metodi Config ---
    @Override
    public boolean isRaccoltaDisponibilitaChiusa() {
        return configService.isRaccoltaDisponibilitaChiusa();
    }

    @Override
    public void riapriRaccoltaDisponibilita() {
        configService.riapriRaccoltaDisponibilita();
    }

    // --- Metodi Visite ---
    @Override
    public List<Visita> getVisitePreviewByStato(Visita.StatoVisita stato) {
        return visitaService.getVisitePreviewByStato(stato);
    }

    @Override
    public List<Visita> getVisiteFromArchivio() {
        return visitaService.getVisiteFromArchivio();
    }

    @Override
    public void rimuoviVecchieDisponibilita() {
        visitaService.rimuoviVecchieDisponibilita();
    }

    @Override
    public void creaPiano() {
        visitaService.creaPiano();
    }

    @Override
    public List<Visita> getVisitePreviewByFruitore(Visita.StatoVisita stato, String nomeFruitore) {
        return visitaService.getVisitePreviewByFruitore(stato, nomeFruitore);
    }

    @Override
    public List<String> getCodiciPrenotazioneFruitorePerVisita(String nomeFruitore, int visitaId) {
        return visitaService.getCodiciPrenotazioneFruitorePerVista(nomeFruitore, visitaId);
    }
}
