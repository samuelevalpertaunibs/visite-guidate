package com.unibs.services;

import java.util.List;
import com.unibs.daos.VolontarioDao;
import com.unibs.models.Volontario;

public class VolontarioService {

    public List<Volontario> getAllVolontari() {
        return VolontarioDao.getAllVolontari();
    }

    public int getIdByUsername(String volontariNome) {
        return VolontarioDao.getIdByUsername(volontariNome);
    }
}
