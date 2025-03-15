package com.unibs.models;

import java.util.ArrayList;

public class Config {

    private final ArrayList<Comune> ambitoTerritoriale;
    private int numeroMassimoIscrizioniPrenotazione;

    public Config(ArrayList<Comune> ambitoTerritoriale, int numeroMassimoIscrizioniPrenotazione) {
        this.ambitoTerritoriale = ambitoTerritoriale;
        this.numeroMassimoIscrizioniPrenotazione = numeroMassimoIscrizioniPrenotazione;
    }

    public ArrayList<Comune> getAmbitoTerritoriale() {
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
}
