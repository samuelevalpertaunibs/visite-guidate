package com.unibs;

import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.util.ArrayList;

public class LuogoController {
    private final LuogoService luogoService;
    private final View view;

    protected LuogoController(View view) {
        this.luogoService = new LuogoService();
        this.view = view;
    }

    public Luogo selezionaLuogo() {
        ArrayList<Luogo> luoghi = luogoService.getAllLuoghi();
        if (luoghi == null || luoghi.isEmpty()) return aggiungiLuogo();

        int size = luoghi.size();
        String[] luoghi_string = new String[size + 1];
        for (int i = 0; i < size; i++) {
            luoghi_string[i] = luoghi.get(i).toString();
        }
        luoghi_string[size] = "Aggiungi nuovo luogo";

        int scelta = view.showMenu("Seleziona un luogo: ", luoghi_string);

        if (scelta == size - 1) return aggiungiLuogo();

        return luoghi.get(scelta);
    }

    private Luogo aggiungiLuogo() {
        ComuneController comuneController = new ComuneController(view);
        Luogo luogo;
        do {
            view.clearScreenAndShowTitle("Inserisci un nuovo luogo");
            String nome = view.getLimitedInput("Inserisci il nome del luogo: ", 64);
            String descrizione = view.getLimitedInput("Inserisci la descrizione del luogo: ", 256);
            Comune comune = comuneController.selezionaComune();

            try {
                luogo = luogoService.aggiungiLuogo(new Luogo(nome, descrizione, comune));
            } catch (IllegalArgumentException e) {
                view.clearScreen(e.getMessage());
                continue;
            } catch (DatabaseException e) {
                view.clearScreen("Errore durante l'aggiunta del luogo nel database: " + e.getMessage());
                continue;
            } catch (Exception e) {
                view.clearScreen("Errore sconosciuto: " + e.getMessage());
                continue;
            }
            return luogo;

        } while (true);
    }

}
