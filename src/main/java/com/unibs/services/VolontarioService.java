package com.unibs.services;

import com.unibs.daos.UtenteDao;
import com.unibs.models.Giorno;
import com.unibs.models.TipoVisita;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;

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
    final UtenteDao utenteDao = new UtenteDao();

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

    public Set<LocalDate> calcolaDateDiCuiRichiedereDisponibilitaPerVolontario(Volontario volontario, YearMonth mese) throws DatabaseException {
        Set<LocalDate> risultati = new HashSet<>();
        int volontarioId = volontario.getId();

        try {
            Set<LocalDate> datePrecluse = datePrecluseService.findByMonth(mese);
            for (TipoVisita visita : tipoVisitaService.findByVolontario(volontarioId)) {

                LocalDate start = visita.dataInizio();
                LocalDate end = visita.dataFine();

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
            throw new DatabaseException("Errore nel recupero delle date selezionabili.");
        }

        return risultati;
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

    public List<LocalDate> getDateDisponibiliByMese(Volontario volontario, YearMonth mese) throws DatabaseException {
        try {
            return utenteDao.getDateDisponibiliByMese(volontario.getId(), mese);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle date disponibili", e);
            throw new DatabaseException("Impossibile recuperare le date disponibili selezionate.");
        }
    }

    public void rimuoviNonAssociati() {
        try {
            List<Integer> volontariNonAssociati = utenteDao.getIdVolontariNonAssociati();
            for (Integer id : volontariNonAssociati) {
                utenteDao.rimuovi(id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione dei volontari non associati", e);
            throw new DatabaseException("Impossibile rimuovere i volontari non associati ad alcuna visita.");
        }
    }

    public void rimuoviByNome(String nome) {
        try {
            utenteDao.rimuovi(nome);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la rimozione del volontario per nome: " + nome, e);
            throw new DatabaseException("Errore durante la rimozione del volontario.");
        }
    }

    public Set<Volontario> getVolontariNonAssociatiByTipoVisitaId(int tipoVisitaId) {
        try {
            return utenteDao.getVolontariNonAssociatiByTipoVisitaId(tipoVisitaId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero dei volontari", e);
            throw new DatabaseException("Errore durante il recupero dei volontari.");
        }
    }

    public void associaATipoVisita(Set<Volontario> volontariSelezionati, int tipoVisitaId) {
        try {
            for (Volontario volontario : volontariSelezionati) {
                utenteDao.associaATipoVisitaById(volontario.getId(), tipoVisitaId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'associazione dei volontari", e);
            throw new DatabaseException("Errore durante l'associazione dei volontari.");
        }
    }
}
