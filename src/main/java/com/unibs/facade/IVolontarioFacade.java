package com.unibs.facade;

import com.unibs.models.Volontario;
import com.unibs.models.Visita;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public interface IVolontarioFacade {

    // --- Config ---
    boolean regimeAttivo();
    boolean raccoltaDisponibilitaChiusa();
    YearMonth getMesePeriodoCorrente();

    // --- Disponibilità volontario ---
    Set<LocalDate> calcolaDateDiCuiRichiedereDisponibilita(Volontario volontario, YearMonth mese);
    List<LocalDate> getDateDisponibiliByMese(Volontario volontario, YearMonth mese);
    void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate);

    // --- Visite ---
    List<Visita> getVisitePreviewByVolontario(Visita.StatoVisita stato, Volontario volontario);
    List<String> getCodiciPrenotazionePerVisita(Volontario volontario, int visitaId);
}
