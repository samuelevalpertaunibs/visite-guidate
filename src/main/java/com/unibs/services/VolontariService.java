package com.unibs.services;

import java.util.ArrayList;

import com.unibs.daos.VolontariDao;

public class VolontariService {

    // public VolontariService() {
    // };

    public ArrayList<String> getListaVolontari() {
        return VolontariDao.getListaVolontari();
    }

    public int getIdByUsername(String volontariNome) { return VolontariDao.getIdByUsername(volontariNome); }
}
