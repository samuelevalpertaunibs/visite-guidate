package com.unibs.services;

import com.unibs.DatabaseException;
import com.unibs.daos.UtenteDao;
import com.unibs.models.Giorno;
import com.unibs.models.TipoVisita;
import com.unibs.models.Volontario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VolontarioService {
    private static final Logger LOGGER = Logger.getLogger(VolontarioService.class.getName());
    private final TipoVisitaService tipoVisitaService;
    private final GiornoService giornoService;
    private final DatePrecluseService datePrecluseService;
    UtenteDao utenteDao = new UtenteDao();

    public VolontarioService(TipoVisitaService tipoVisitaService, GiornoService giornoService, DatePrecluseService datePrecluseService) {
        this.tipoVisitaService = tipoVisitaService;
        this.giornoService = giornoService;
        this.datePrecluseService = datePrecluseService;
    }

    public List<Volontario> findAllVolontari() throws DatabaseException {
        try {
            return utenteDao.getAllVolontari();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta della data preclusa", e);
            throw new DatabaseException("Errore nel recupero dei volontari");
        }
    }

    public Set<LocalDate> getDateDisponibiliPerVolontario(Volontario volontario) throws DatabaseException {

        YearMonth mese = calcolaMeseEntrante();
        Set<LocalDate> risultati = new HashSet<>();
        int volontarioId = volontario.getId();

        try {
            Set<LocalDate> datePrecluse = datePrecluseService.findByMonth(mese);
            for (TipoVisita visita : tipoVisitaService.findByVolontario(volontarioId)) {

                LocalDate start = visita.getDataInizio();
                LocalDate end = visita.getDataFine();

                Set<Giorno> giorni = visita.getGiorniSettimana();

                LocalDate primoGiornoMese = mese.atDay(1);
                LocalDate ultimoGiornoMese = mese.atEndOfMonth();

                // Intersezione tra (dataInizio, dataFine) e mese
                LocalDate inizio = primoGiornoMese.isAfter(start) ? primoGiornoMese : start;
                LocalDate fine = ultimoGiornoMese.isBefore(end) ? ultimoGiornoMese : end;

                for (LocalDate giorno = inizio; !giorno.isAfter(fine); giorno = giorno.plusDays(1)) {
                    Giorno giornoItaliano = giornoService.fromDayOfWeek(giorno.getDayOfWeek());
                    if (giorni.contains(giornoItaliano) && !datePrecluse.contains(giorno)) {
                        risultati.add(giorno);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle date selezionabili", e);
            throw new DatabaseException("Errore nel recupero delle date selezionabili");
        }

        return risultati;
    }

    private YearMonth calcolaMeseEntrante() {
        LocalDate oggi = LocalDate.now();

        if (oggi.getDayOfMonth() <= 15) {
            return YearMonth.from(oggi.plusMonths(1));
        } else {
            return YearMonth.from(oggi.plusMonths(2));
        }
    }

    public void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate) throws DatabaseException {
        try {
            utenteDao.sovrascriviDisponibilita(volontario, selezionate);
        } catch (SQLException e) {
            throw new DatabaseException("Impossibile salvare le disponibilità");
        }
    }

    public Set<Volontario> getByTipoVisitaId(int tipoVisitaId) throws DatabaseException {
        try {
            return utenteDao.findVolontariByTipoVisitaId(tipoVisitaId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei volontari", e);
            throw new DatabaseException("Impossibile recuperare i volontari");

        }
    }
}
