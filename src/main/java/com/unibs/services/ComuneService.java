package com.unibs.services;

import com.unibs.daos.ComuneDao;
import com.unibs.models.Comune;

import java.util.ArrayList;

public class ComuneService {
    public ArrayList<Comune> getAllComuni() {
        return ComuneDao.getAllComuni();
    }

}
