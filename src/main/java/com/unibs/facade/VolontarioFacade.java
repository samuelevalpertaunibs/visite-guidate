package com.unibs.facade;

import com.unibs.models.Volontario;
import com.unibs.models.Visita;
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

    @Override
    public boolean regimeAttivo() {
        return configService.regimeAttivo();
    }

    @Override
    public boolean raccoltaDisponibilitaChiusa() {
        return configService.isRaccoltaDisponibilitaChiusa();
    }

    @Override
    public YearMonth getMesePeriodoCorrente() {
        return configService.getMesePeriodoCorrente();
    }

    @Override
    public Set<LocalDate> calcolaDateDiCuiRichiedereDisponibilita(Volontario volontario, YearMonth mese) {
        return volontarioService.calcolaDateDiCuiRichiedereDisponibilitaPerVolontario(volontario, mese);
    }

    @Override
    public List<LocalDate> getDateDisponibiliByMese(Volontario volontario, YearMonth mese) {
        return volontarioService.getDateDisponibiliByMese(volontario, mese);
    }

    @Override
    public void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate) {
        volontarioService.sovrascriviDisponibilita(volontario, selezionate);
    }

    @Override
    public List<Visita> getVisitePreviewByVolontario(Visita.StatoVisita stato, Volontario volontario) {
        return visitaService.getVisitePreviewByVolontario(stato, volontario);
    }

    @Override
    public List<String> getCodiciPrenotazionePerVisita(Volontario volontario, int visitaId) {
        return visitaService.getCodiciPrenotazionePerVista(volontario, visitaId);
    }
}
