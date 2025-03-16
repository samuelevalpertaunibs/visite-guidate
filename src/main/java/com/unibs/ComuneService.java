package com.unibs;

import com.unibs.models.Comune;

import java.util.ArrayList;

public class ComuneService {
    public ArrayList<Comune> getAllComuni() {
        return ComuneDao.getAllComuni();
    }

}
