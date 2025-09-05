package com.unibs.facades;

import com.unibs.models.Visita;
import com.unibs.models.Volontario;
import com.unibs.services.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public class VolontarioFacade implements IVolontarioFacade {

    private final ConfigService configService;
    private final VolontarioService volontarioService;
    private final VisitaService visitaService;

    public VolontarioFacade(ServiceFactory serviceFactory) {
        this.configService = serviceFactory.getConfigService();
        this.volontarioService = serviceFactory.getVolontarioService();
        this.visitaService = serviceFactory.getVisitaService();
    }

    /** Controlla se il regime è attivo */
    @Override
    public boolean regimeAttivo() {
        return configService.regimeAttivo();
    }

    /** Controlla se la raccolta disponibilità è chiusa */
    @Override
    public boolean raccoltaDisponibilitaChiusa() {
        return configService.isRaccoltaDisponibilitaChiusa();
    }

    /** Recupera il mese corrente del periodo configurato */
    @Override
    public YearMonth getMesePeriodoCorrente() {
        return configService.getMesePeriodoCorrente();
    }

    /** Calcola le date da chiedere come disponibilità */
    @Override
    public Set<LocalDate> calcolaDateDiCuiRichiedereDisponibilita(Volontario volontario, YearMonth mese) {
        return volontarioService.calcolaDateDiCuiRichiedereDisponibilitaPerVolontario(volontario, mese);
    }

    /** Recupera le date attualmente disponibili di un volontario */
    @Override
    public List<LocalDate> getDateDisponibiliByMese(Integer volontarioId, YearMonth mese) {
        return volontarioService.getDateDisponibiliByMese(volontarioId, mese);
    }

    /** Sovrascrive le disponibilità del volontario */
    @Override
    public void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate) {
        volontarioService.sovrascriviDisponibilita(volontario, selezionate);
    }

    /** Recupera le visite in base allo stato e al volontario */
    @Override
    public List<Visita> getVisitePreviewByVolontario(Visita.StatoVisita stato, Volontario volontario) {
        return visitaService.getVisitePreviewByVolontario(stato, volontario);
    }

    /** Recupera i codici prenotazione associati a un volontario per una visita */
    @Override
    public List<String> getCodiciPrenotazionePerVisita(Volontario volontario, int visitaId) {
        return visitaService.getCodiciPrenotazionePerVisita(volontario, visitaId);
    }
}
