package com.unibs.facades;

import com.unibs.models.Visita;
import java.util.List;

public interface IVisitaFacade {

    // --- Metodi Config ---
    boolean isRaccoltaDisponibilitaChiusa();
    void riapriPiano();

    // --- Metodi Visite ---
    List<Visita> getVisitePreviewByStato(Visita.StatoVisita stato);
    List<Visita> getVisiteFromArchivio();
    void creaPiano();
    List<Visita> getVisitePreviewByFruitore(Visita.StatoVisita stato, String nomeFruitore);
    List<String> getCodiciPrenotazioneFruitorePerVisita(String nomeFruitore, int visitaId);
}
