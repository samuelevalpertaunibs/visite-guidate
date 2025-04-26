package com.unibs.models;

import java.time.LocalDate;
import java.util.List;

public class Config {

    private List<Comune> ambitoTerritoriale;
    private int numeroMassimoIscrizioniPrenotazione;
    private final LocalDate initializedOn;

    public Config(List<Comune> ambitoTerritoriale, int numeroMassimoIscrizioniPrenotazione, LocalDate initializedOn) {
        this.ambitoTerritoriale = ambitoTerritoriale;
        this.numeroMassimoIscrizioniPrenotazione = numeroMassimoIscrizioniPrenotazione;
        this.initializedOn = initializedOn;
    }

    public List<Comune> getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    public int getNumeroMassimoIscrizioniPrenotazione() {
        return numeroMassimoIscrizioniPrenotazione;
    }

    public void setNumeroMassimoIscrizioniPrenotazione(int numeroMassimoIscrizioniPrenotazione) {
        this.numeroMassimoIscrizioniPrenotazione = numeroMassimoIscrizioniPrenotazione;
    }

    public boolean doesInclude(Comune comuneDaControllare) {
        for (Comune comune : this.ambitoTerritoriale) {
            if (comune.equals(comuneDaControllare))
                return true;
        }
        return false;
    }

    public boolean isAmbitoTerritorialeVuoto() {
        return ambitoTerritoriale.isEmpty();
    }

    public LocalDate getInitializedOn() {
        return initializedOn;
    }


    public void setAmbitoTerritoriale(List<Comune> ambitoTerritoriale) {
        this.ambitoTerritoriale = ambitoTerritoriale;
    }

    public int getNumeroComuni() {
        return ambitoTerritoriale.size();
    }
}
