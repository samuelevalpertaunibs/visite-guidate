package com.unibs.facade;

import com.unibs.models.Visita;
import java.util.List;

public interface IVisitaFacade {

    // --- Metodi Config ---
    boolean isRaccoltaDisponibilitaChiusa();
    void riapriRaccoltaDisponibilita();

    // --- Metodi Visite ---
    List<Visita> getVisitePreviewByStato(Visita.StatoVisita stato);
    List<Visita> getVisiteFromArchivio();
    void rimuoviVecchieDisponibilita();
    void creaPiano();
    List<Visita> getVisitePreviewByFruitore(Visita.StatoVisita stato, String nomeFruitore);
    List<String> getCodiciPrenotazioneFruitorePerVisita(String nomeFruitore, int visitaId);
}
