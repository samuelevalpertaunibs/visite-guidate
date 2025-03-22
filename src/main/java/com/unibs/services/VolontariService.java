package com.unibs.services;

import java.util.ArrayList;

import com.unibs.daos.VolontariDao;

/**
 * volontariService
 */
public class VolontariService {

    // public VolontariService() {
    // };

    public ArrayList<String> getListaVolontari() {
        return VolontariDao.getListaVolontari();
    }
}
