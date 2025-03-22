package com.unibs.controllers;

import java.util.ArrayList;

import com.unibs.services.VolontariService;

public class VolontariController {

    private final VolontariService volontariService;

    public VolontariController() {
        this.volontariService = new VolontariService();
    }

    public ArrayList<String> getListaVolontari() { return volontariService.getListaVolontari(); }

    public int getIdByUsername(String volontariNome) { return volontariService.getIdByUsername(volontariNome); }
}