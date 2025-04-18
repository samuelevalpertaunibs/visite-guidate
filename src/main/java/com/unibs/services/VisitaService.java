package com.unibs.services;
import com.unibs.daos.VisitaDao;
import com.unibs.models.Visita;

import java.util.List;

public class VisitaService {
    public List<Visita> getVisiteByStato(Visita.StatoVisita stato) {
        return VisitaDao.getVisiteByStato(stato);
    }
}
