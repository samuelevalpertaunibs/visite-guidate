package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.services.LuogoService;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;
import com.unibs.views.AggiungiLuogoView;
import com.unibs.views.ElencoLuoghiView;
import java.util.List;
import java.util.function.Consumer;

public class LuogoController {
    private final LuogoService luogoService;
    private final WindowBasedTextGUI gui;
    private final ElencoLuoghiView elencoLuoghiView;



    protected LuogoController(WindowBasedTextGUI gui) {
        this.luogoService = new LuogoService();
        this.elencoLuoghiView = new ElencoLuoghiView(this);
        this.gui = gui;
    }

    public List<Luogo> getLuoghi() {
        return luogoService.getAllLuoghi();
    }

    public void creaInterfacciaAggiungiLuogo(Consumer<Luogo> onLuogoCreated) {
        // Crea la vista AggiungiLuogoView, passando la callback
        AggiungiLuogoView view = new AggiungiLuogoView(this, new ConfigController(this.gui), nuovoLuogo -> {
            // Quando un luogo viene creato, chiama il Consumer passato
            if (onLuogoCreated != null) {
                onLuogoCreated.accept(nuovoLuogo); // Passa il luogo alla callback
            }
        });

        // Aggiungi la finestra alla GUI e aspetta che venga chiusa
        this.gui.addWindowAndWait(view.creaFinestra());
    }

    public WindowBasedTextGUI getGui() {
        return gui;
    }

    public Luogo aggiungiLuogo(String nome, String descrizione, Comune comune) {
        return luogoService.aggiungiLuogo(new Luogo(-1, nome, descrizione, comune));
    }

    public void apriVisualizzaElencoLuoghi() {
        gui.addWindowAndWait(elencoLuoghiView.creaFinestra());
    }
}
