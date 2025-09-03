package com.unibs.facade;

import com.unibs.models.Visita;
import com.unibs.services.ConfigService;
import com.unibs.services.ServiceFactory;
import com.unibs.services.VisitaService;

import java.util.List;

public class VisitaFacade {

    private final VisitaService visitaService;
    private final ConfigService configService;

    public VisitaFacade(ServiceFactory serviceFactory) {
        this.visitaService = serviceFactory.getVisitaService();
        this.configService = serviceFactory.getConfigService();
    }

    // --- Metodi Config ---
    public boolean isRaccoltaDisponibilitaChiusa() {
        return configService.isRaccoltaDisponibilitaChiusa();
    }

    public void riapriRaccoltaDisponibilita() {
        configService.riapriRaccoltaDisponibilita();
    }

    // --- Metodi Visite ---
    public List<Visita> getVisitePreviewByStato(Visita.StatoVisita stato) {
        return visitaService.getVisitePreviewByStato(stato);
    }

    public List<Visita> getVisiteFromArchivio() {
        return visitaService.getVisiteFromArchivio();
    }

    public void rimuoviVecchieDisponibilita() {
        visitaService.rimuoviVecchieDisponibilita();
    }

    public void creaPiano() {
        visitaService.creaPiano();
    }

    public List<Visita> getVisitePreviewByFruitore(Visita.StatoVisita stato, String nomeFruitore) {
        return visitaService.getVisitePreviewByFruitore(stato, nomeFruitore);
    }

    public List<String> getCodiciPrenotazioneFruitorePerVisita(String nomeFruitore, int visitaId) {
        return visitaService.getCodiciPrenotazioneFruitorePerVista(nomeFruitore, visitaId);
    }
}
