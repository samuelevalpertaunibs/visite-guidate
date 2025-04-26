package com.unibs.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.unibs.daos.TipoVisitaDao;
import com.unibs.daos.VolontarioDao;
import com.unibs.models.TipoVisita;
import com.unibs.models.Volontario;

public class VolontarioService {

    public List<Volontario> getAllVolontari() {
        return VolontarioDao.getAllVolontari();
    }

    public int getIdByUsername(String volontariNome) {
        return VolontarioDao.getIdByUsername(volontariNome);
    }

    public List<LocalDate> getDateSelezionabiliPerVolontario (int volontarioId) {
        // 1. seleziono tutti i tipi di visita che hanno almeno un giorno compreso nel mese prossimo legati al volontario
        // 2. itero tutte le visite e aggiungo i giorni guardando la tabella NN
        // 3. ordino i giorni
        List<LocalDate> dateSelezionabili = new ArrayList<>();

        // 1. Seleziono solo gli id, il resto non mi serve
        LocalDate now = LocalDate.now();
        int mese = now.getDayOfMonth() > 15 ? now.getMonthValue() + 1 : now.getMonthValue();

        List<TipoVisita> tipiVisita = TipoVisitaDao.getByVolontarioIdAndMese(volontarioId, mese);

        for (Integer id : tipiVisitaIds) {

        }

        return dateSelezionabili;
    }
}
