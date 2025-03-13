package com.unibs.models;

public class Config {

    private final Comune[] ambitoTerritoriale;
    private int numeroMassimoIscrizioniPrenotazione;

    public Config(Comune[] ambitoTerritoriale, int numeroMassimoIscrizioniPrenotazione) {
        this.ambitoTerritoriale = ambitoTerritoriale;
        this.numeroMassimoIscrizioniPrenotazione = numeroMassimoIscrizioniPrenotazione;
    }

    public Comune[] getAmbienteTerritoriale() {
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
}
