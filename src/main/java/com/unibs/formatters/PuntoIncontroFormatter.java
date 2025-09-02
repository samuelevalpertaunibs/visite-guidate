package com.unibs.formatters;

import com.unibs.models.PuntoIncontro;

public class PuntoIncontroFormatter implements Formatter<PuntoIncontro> {

    @Override
    public String format(PuntoIncontro pi) {
        return pi.getIndirizzo() + ", " + pi.getComune() + " (" + pi.getProvincia() + ")";
    }
}
