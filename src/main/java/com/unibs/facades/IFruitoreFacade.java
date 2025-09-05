package com.unibs.facades;

import com.unibs.models.Fruitore;
import com.unibs.models.Visita;

import java.util.List;

public interface IFruitoreFacade {
    boolean isRegimeAttivo();
    List<Visita> getVisiteProposte();
    void disdiciVisita(Fruitore fruitore, int codiceIscrizione);
    int iscriviAdUnaVisita(Fruitore fruitore, Visita visita, int numeroIscritti);
}
