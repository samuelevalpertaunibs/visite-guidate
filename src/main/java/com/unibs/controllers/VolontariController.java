package com.unibs.controllers;

import java.util.ArrayList;

import com.unibs.models.VolontariService;

/**
 * VolontariController
 */
public class VolontariController {

    private final VolontariService volontariService;

    public VolontariController() {
        this.volontariService = new VolontariService();
    }

    public ArrayList<String> getListaVolontari() {
        return volontariService.getListaVolontari();
    }

}
