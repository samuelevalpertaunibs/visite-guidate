package com.unibs.models;

public class AmbitoTerritoriale {

    private Regione regione;
    private Provincia provincia;

    public AmbitoTerritoriale(Regione regione, Provincia provincia) {
        this.regione = regione;
        this.provincia = provincia;
    }

    public boolean contieneLuogo(Luogo luogo) {
        return this.regione.equals(luogo.getRegione()) && this.provincia.equals(luogo.getProvincia());
    }
}
