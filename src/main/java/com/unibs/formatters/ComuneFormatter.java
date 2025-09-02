package com.unibs.formatters;

import com.unibs.models.Comune;

public class ComuneFormatter implements Formatter<Comune> {

    @Override
    public String format(Comune c) {
        return String.format("%s (%s), %s", c.getNome(), c.getProvincia(), c.getRegione());
    }
}
