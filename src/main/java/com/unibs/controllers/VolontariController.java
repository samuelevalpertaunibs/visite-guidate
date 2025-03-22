package com.unibs.controllers;

import java.util.List;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.Volontario;
import com.unibs.services.VolontarioService;

public class VolontariController {
    private final VolontarioService volontarioService;
    private final WindowBasedTextGUI tui;

    public VolontariController(WindowBasedTextGUI tui) {
        this.volontarioService = new VolontarioService();
        this.tui = tui;
    }

    public List<Volontario> getAllVolontari() {
        return volontarioService.getAllVolontari();
    }

    public int getIdByUsername(String nomeVolontario) {
        return volontarioService.getIdByUsername(nomeVolontario);
    }

}
