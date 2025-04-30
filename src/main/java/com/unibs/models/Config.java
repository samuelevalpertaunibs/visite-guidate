package com.unibs.models;

import java.time.LocalDate;
import java.util.List;

public class Config {

    private List<Comune> ambitoTerritoriale;
    private final int numeroMassimoIscrizioniPrenotazione;
    private final LocalDate periodoCorrente;

    public Config(List<Comune> ambitoTerritoriale, int numeroMassimoIscrizioniPrenotazione, LocalDate periodoCorrente) {
        this.ambitoTerritoriale = ambitoTerritoriale;
        this.numeroMassimoIscrizioniPrenotazione = numeroMassimoIscrizioniPrenotazione;
        this.periodoCorrente = periodoCorrente;
    }

    public List<Comune> getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    public int getNumeroMassimoIscrizioniPrenotazione() {
        return numeroMassimoIscrizioniPrenotazione;
    }

    public boolean doesInclude(Comune comuneDaControllare) {
        for (Comune comune : ambitoTerritoriale) {
            if (comune.equals(comuneDaControllare))
                return true;
        }
        return false;
    }

    public LocalDate getPeriodoCorrente() {
        return periodoCorrente;
    }

    public void setAmbitoTerritoriale(List<Comune> ambitoTerritoriale) {
        this.ambitoTerritoriale = ambitoTerritoriale;
    }

    public int getNumeroComuni() {
        return ambitoTerritoriale.size();
    }
}
