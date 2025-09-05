package com.unibs.facades;

import com.unibs.models.Visita;
import com.unibs.models.Volontario;
import com.unibs.services.ConfigService;
import com.unibs.services.ServiceFactory;
import com.unibs.services.VisitaService;
import com.unibs.services.VolontarioService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public class VolontarioFacade {

    private final ConfigService configService;
    private final VolontarioService volontarioService;
    private final VisitaService visitaService;

    public VolontarioFacade(ServiceFactory serviceFactory) {
        this.configService = serviceFactory.getConfigService();
        this.volontarioService = serviceFactory.getVolontarioService();
        this.visitaService = serviceFactory.getVisitaService();
    }

    /** Controlla se il regime è attivo */
    public boolean regimeAttivo() {
        return configService.regimeAttivo();
    }

    /** Controlla se la raccolta disponibilità è chiusa */
    public boolean raccoltaDisponibilitaChiusa() {
        return configService.isRaccoltaDisponibilitaChiusa();
    }

    /** Recupera il mese corrente del periodo configurato */
    public YearMonth getMesePeriodoCorrente() {
        return configService.getMesePeriodoCorrente();
    }

    /** Calcola le date da chiedere come disponibilità */
    public Set<LocalDate> calcolaDateDiCuiRichiedereDisponibilita(Volontario volontario, YearMonth mese) {
        return volontarioService.calcolaDateDiCuiRichiedereDisponibilitaPerVolontario(volontario, mese);
    }

    /** Recupera le date attualmente disponibili di un volontario */
    public List<LocalDate> getDateDisponibiliByMese(Integer volontarioId, YearMonth mese) {
        return volontarioService.getDateDisponibiliByMese(volontarioId, mese);
    }

    /** Sovrascrive le disponibilità del volontario */
    public void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate) {
        volontarioService.sovrascriviDisponibilita(volontario, selezionate);
    }

    /** Recupera le visite in base allo stato e al volontario */
    public List<Visita> getVisitePreviewByVolontario(Visita.StatoVisita stato, Volontario volontario) {
        return visitaService.getVisitePreviewByVolontario(stato, volontario);
    }

    /** Recupera i codici prenotazione associati a un volontario per una visita */
    public List<java.lang.String> getCodiciPrenotazionePerVisita(Volontario volontario, int visitaId) {
        return visitaService.getCodiciPrenotazionePerVista(volontario, visitaId);
    }
}
