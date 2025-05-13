package com.unibs.services;

import com.unibs.daos.UtenteDao;
import com.unibs.models.Giorno;
import com.unibs.models.TipoVisita;
import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
    private static final String DEFAULT_PASSWORD = "password";
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

    public void aggiungiVolontario(String username) {
        if (utenteDao.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username già esistente.");
        }

        try {
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(DEFAULT_PASSWORD, salt);

            Utente nuovoVolontario = new Utente(0, username, hashedPassword, salt, 2, null);
            utenteDao.inserisciUtente(nuovoVolontario);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del volontario.", e);
            throw new DatabaseException("Impossibile aggiungere il volontario.");
        }
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private String hashPassword(String password, byte[] salt) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashedPassword);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
