package com.unibs;

import com.unibs.models.Luogo;

import java.util.ArrayList;


public class LuogoService {
    public ArrayList<Luogo> getAllLuoghi() {
        return LuogoDao.getAllLuoghi();
    }

    public Luogo aggiungiLuogo(Luogo luogoDaAggiungere) {
        if (luogoDaAggiungere.getNome().isBlank() || luogoDaAggiungere.getDescrizione().isBlank())
            throw new IllegalArgumentException("I campi non possono essere vuoti.");

        if (LuogoDao.esisteLuogo(luogoDaAggiungere.getNome())) {
            throw new IllegalArgumentException("Il luogo è già presente.");
        }

        return LuogoDao.aggiungiLuogo(luogoDaAggiungere);
    }
}
