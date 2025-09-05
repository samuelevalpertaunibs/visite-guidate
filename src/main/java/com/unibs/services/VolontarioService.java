package com.unibs.services;

import com.unibs.daos.UtenteDao;
import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VolontarioService {
    private static final Logger LOGGER = Logger.getLogger(VolontarioService.class.getName());
    private static final java.lang.String DEFAULT_PASSWORD = "password";

    private final PasswordService passwordService;
    private final DisponibilitaCalculatorService disponibilitaCalculatorService;
    private final UtenteDao utenteDao;

    public VolontarioService(UtenteDao utenteDao, TipoVisitaService tipoVisitaService, GiornoService giornoService, DatePrecluseService datePrecluseService) {
        this.passwordService = new PasswordService();
        this.utenteDao = utenteDao;
        this.disponibilitaCalculatorService = new DisponibilitaCalculatorService(tipoVisitaService, giornoService, datePrecluseService);
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
        return disponibilitaCalculatorService.calcolaDateDisponibili(volontario, mese);
    }

    public void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate) throws DatabaseException {
        try {
            utenteDao.sovrascriviDisponibilita(volontario, selezionate);
        } catch (SQLException e) {
            throw new DatabaseException("Impossibile salvare le disponibilità");
        }
    }

    public HashMap<Integer, String> getByTipoVisitaId(int tipoVisitaId) throws DatabaseException {
        try {
            return utenteDao.findVolontariByTipoVisitaId(tipoVisitaId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei volontari", e);
            throw new DatabaseException("Impossibile recuperare i volontari");

        }
    }

    public List<LocalDate> getDateDisponibiliByMese(Integer volontarioId, YearMonth mese) throws DatabaseException {
        try {
            return utenteDao.getDateDisponibiliByMese(volontarioId, mese);
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

    public HashMap<Integer, String> getVolontariNonAssociatiByTipoVisitaId(int tipoVisitaId) {
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

    public void applicaRimozioneVolontari() throws DatabaseException {
            try {
                utenteDao.applicaRimozioneVolontari();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione dei volontari eliminati", e);
                throw new DatabaseException("Impossibile rimuovere i volontari eliminati.");
            }
    }

    public void inserisciVolontarioDaRimuovere(java.lang.String nome) throws DatabaseException {
        try {
            int id = utenteDao.getIdByUsername(nome).orElseThrow(Exception::new);
            utenteDao.inserisciVolontarioDaRimuovere(id);
            utenteDao.terminaTVAssociatiAlVolontario(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione del tipo di visita.", e);
            throw new DatabaseException("Impossibile rimuovere il tipo di visita.");
        }
    }

    public void aggiungiVolontario(java.lang.String username) {
        if(utenteDao.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username già esistente");
        }
        try{
            byte[] salt = passwordService.generateSalt();
            java.lang.String hashedPassword = passwordService.hashPassword(DEFAULT_PASSWORD, salt);

            Utente nuovoVolontario = new Utente(0, username, hashedPassword, salt, 2, null);
            utenteDao.inserisciUtente(nuovoVolontario);
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del volontario.", e);
            throw new DatabaseException("Impossibile aggiunta del volontario.");
        }
    }
}