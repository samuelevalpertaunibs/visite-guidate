package com.unibs.facade;

import com.unibs.models.Fruitore;
import com.unibs.models.Visita;

import java.util.List;

public interface IFruitoreFacade {
    boolean regimeAttivo();
    List<Visita> getVisitePreviewByStato(Visita.StatoVisita stato);
    void disdici(Fruitore fruitore, int codiceIscrizione);
    int iscrivi(Fruitore fruitore, Visita visita, int numeroIscritti);
}
