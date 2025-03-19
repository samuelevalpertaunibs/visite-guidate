package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.LuogoService;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;
import com.unibs.views.AggiungiLuogoView;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LuogoController {
    private final LuogoService luogoService;
    private final WindowBasedTextGUI gui;

    protected LuogoController(WindowBasedTextGUI gui) {
        this.luogoService = new LuogoService();
        this.gui = gui;
    }

//    public Luogo selezionaLuogo() {
//        ArrayList<Luogo> luoghi = luogoService.getAllLuoghi();
//        if (luoghi == null || luoghi.isEmpty()) return aggiungiLuogo();
//
//        int size = luoghi.size();
//        String[] luoghi_string = new String[size + 1];
//        for (int i = 0; i < size; i++) {
//            luoghi_string[i] = luoghi.get(i).toString();
//        }
//        luoghi_string[size] = "Aggiungi nuovo luogo";
//
//        int scelta = view.showMenu("Seleziona un luogo: ", luoghi_string);
//
//        if (scelta == size - 1) return aggiungiLuogo();
//
//        return luoghi.get(scelta);
//    }
//
//    private Luogo aggiungiLuogo() {
//        ComuneController comuneController = new ComuneController(view);
//        Luogo luogo;
//        do {
//            view.clearScreenAndShowTitle("Inserisci un nuovo luogo");
//            String nome = view.getLimitedInput("Inserisci il nome del luogo: ", 64);
//            String descrizione = view.getLimitedInput("Inserisci la descrizione del luogo: ", 256);
//            Comune comune = comuneController.selezionaComune();
//
//            try {
//                luogo = luogoService.aggiungiLuogo(new Luogo(nome, descrizione, comune));
//            } catch (IllegalArgumentException e) {
//                view.clearScreen(e.getMessage());
//                continue;
//            } catch (DatabaseException e) {
//                view.clearScreen("Errore durante l'aggiunta del luogo nel database: " + e.getMessage());
//                continue;
//            } catch (Exception e) {
//                view.clearScreen("Errore sconosciuto: " + e.getMessage());
//                continue;
//            }
//            return luogo;
//
//        } while (true);
//    }

    public ArrayList<Luogo> getLuoghi() {
        return luogoService.getAllLuoghi();
    }

    public void creaInterfacciaAggiungiLuogo(Consumer<Luogo> onLuogoCreated) {
        // Crea la vista AggiungiLuogoView, passando la callback
        AggiungiLuogoView view = new AggiungiLuogoView(this, new ConfigController(this.gui), nuovoLuogo -> {
            // Quando un luogo viene creato, chiama il Consumer passato
            if (onLuogoCreated != null) {
                onLuogoCreated.accept(nuovoLuogo);  // Passa il luogo alla callback
            }
        });

        // Aggiungi la finestra alla GUI e aspetta che venga chiusa
        this.gui.addWindowAndWait(view.creaFinestra());
    }


    public WindowBasedTextGUI getGui() {
        return gui;
    }

    public Luogo aggiungiLuogo(String nome, String descrizione, Comune comune) {
        return luogoService.aggiungiLuogo(new Luogo(nome, descrizione, comune));
    }
}
