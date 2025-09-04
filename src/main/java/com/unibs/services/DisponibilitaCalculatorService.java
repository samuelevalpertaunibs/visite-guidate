package com.unibs.services;

import com.unibs.models.Giorno;
import com.unibs.models.Volontario;
import com.unibs.models.TipoVisita;
import com.unibs.utils.DatabaseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

public class DisponibilitaCalculatorService {

    private final TipoVisitaService tipoVisitaService;
    private final GiornoService giornoService;
    private final DatePrecluseService datePrecluseService;

    public DisponibilitaCalculatorService(TipoVisitaService tipoVisitaService, GiornoService giornoService, DatePrecluseService datePrecluseService){
        this.tipoVisitaService = tipoVisitaService;
        this.giornoService = giornoService;
        this.datePrecluseService = datePrecluseService;
    }

    public Set<LocalDate> calcolaDateDisponibili(Volontario volontario, YearMonth mese) throws DatabaseException{
        Set<LocalDate> risultati = new HashSet<>();
        int volontrioId = volontario.getId();

        try{
            Set<LocalDate> datePrecluse = datePrecluseService.findByMonth(mese);
            for(TipoVisita visita : tipoVisitaService.findByVolontario(volontrioId)){
                LocalDate start = visita.getDataInizio();
                LocalDate end = visita.getDataFine();

                Set<Giorno> giorni = visita.getGiorniSettimana();

                LocalDate primoGiornoMese = mese.atDay(1);
                LocalDate ultimoGiornoMese = mese.atEndOfMonth();

                LocalDate inizio = primoGiornoMese.isAfter(start) ? primoGiornoMese : start;
                LocalDate fine = ultimoGiornoMese.isBefore(end) ? ultimoGiornoMese : end;

                for(LocalDate giorno = inizio; !giorno.isAfter(fine); giorno = giorno.plusDays(1)){
                    Giorno giornoItaliano = giornoService.fromDayOfWeek(giorno.getDayOfWeek());
                    if(giorni.contains(giornoItaliano) && !datePrecluse.contains(giorno)){
                        risultati.add(giorno);
                    }
                }
            }
        }catch (Exception e){
            throw new DatabaseException("Errore nel recupero delle date selezionabili", e);
        }
        return risultati;
    }
}