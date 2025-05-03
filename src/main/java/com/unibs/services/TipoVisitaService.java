package com.unibs.services;

import com.unibs.daos.TipoVisitaDao;
import com.unibs.models.*;
import com.unibs.utils.DatabaseException;
import com.unibs.utils.DateService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TipoVisitaService {
    private static final Logger LOGGER = Logger.getLogger(TipoVisitaService.class.getName());
    private static final int MINUTES_PER_DAY = 24 * 60;

    private final TipoVisitaDao tipoVisitaDao;
    private final LuogoService luogoService;
    private final GiornoService giornoService;
    private VolontarioService volontarioService;

    public TipoVisitaService(LuogoService luogoService, GiornoService giornoService) {
        this.tipoVisitaDao = new TipoVisitaDao();
        this.luogoService = luogoService;
        this.giornoService = giornoService;
    }

    public TipoVisita aggiungiTipoVisita(String titolo, String descrizione, String dataInizioString, String dataFineString,
                                         String oraInizioString, String durataMinutiString, boolean entrataLibera, String numeroMinPartecipanti,
                                         String numeroMaxPartecipanti, Luogo luogoSelezionato, Set<Volontario> volontari, Set<Giorno> giorni, String indirizzoPuntoIncontro, String comunePuntoIncontro, String provinciaPuntoIncontro) throws DatabaseException, IllegalArgumentException {
        if (titolo == null || titolo.isEmpty())
            throw new IllegalStateException("Il campo Titolo non può essere vuoto");
        if (tipoVisitaDao.esisteConTitolo(titolo))
            throw new IllegalStateException(titolo + " esiste già");
        if (descrizione == null || descrizione.isEmpty())
            throw new IllegalStateException("Il campo Descrizione non può essere vuoto");
        if (dataInizioString == null || dataInizioString.isEmpty())
            throw new IllegalStateException("Il campo Data inizio non può essere vuoto");
        if (dataFineString == null || dataFineString.isEmpty())
            throw new IllegalStateException("Il campo Data fine non può essere vuoto");
        if (oraInizioString == null || oraInizioString.isEmpty())
            throw new IllegalStateException("Il campo Ora inzio non può essere vuoto");
        if (durataMinutiString == null || durataMinutiString.isEmpty())
            throw new IllegalStateException("Il campo Durata non può essere vuoto");
        if (numeroMinPartecipanti == null || numeroMinPartecipanti.isEmpty())
            throw new IllegalStateException("Il campo Numero minimo partecipanti non può essere vuoto");
        if (numeroMaxPartecipanti == null || numeroMaxPartecipanti.isEmpty())
            throw new IllegalStateException("Il campo Numero massimo partecipanti non può essere vuoto");
        if (luogoSelezionato == null)
            throw new IllegalStateException("Seleziona un luogo prima di preseguire");
        if (volontari == null || volontari.isEmpty())
            throw new IllegalStateException("Seleziona almeno un volontario da associare al tipo di visita.");
        if (giorni == null || giorni.isEmpty())
            throw new IllegalStateException("Seleziona almeno un giorno della settimana.");
        if (indirizzoPuntoIncontro == null || indirizzoPuntoIncontro.isEmpty() || comunePuntoIncontro == null || comunePuntoIncontro.isEmpty() || provinciaPuntoIncontro == null || provinciaPuntoIncontro.isEmpty())
            throw new IllegalStateException("Inserisci tutti i campi relativi al punto d'incontro.");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataInizio;
        LocalDate dataFine;
        try {
            dataInizio = LocalDate.parse(dataInizioString + "/" + DateService.today().getYear(), dateFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Il formato della data di inizio non è corretto");
        }
        try {
            dataFine = LocalDate.parse(dataFineString + "/" + DateService.today().getYear(), dateFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Il formato della data di fine non è corretto");
        }

        if (dataInizio.isAfter(dataFine))
            throw new IllegalArgumentException("La data di fine deve essere successiva a quella di inizio");

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime oraInizio;
        try {
            oraInizio = LocalTime.parse(oraInizioString, timeFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Il formato dell'ora di inizio non è corretto.");
        }

        int durataMinuti;
        try {
            durataMinuti = Integer.parseInt(durataMinutiString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Il formato della durata non è corretto.");
        }

        if (oraInizio.getHour() * 60 + oraInizio.getMinute() + durataMinuti > MINUTES_PER_DAY)
            throw new IllegalArgumentException("La visita deve conludersi ento le 24 del giorno stesso.");

        int numeroMax;
        try {
            numeroMax = Integer.parseInt(numeroMaxPartecipanti);
            if (numeroMax < 1 || numeroMax > 100)
                throw new IllegalArgumentException("Il numero massimo deve essere positivo e minore di 100.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il numero massimo di partecipanti non è valido.");
        }

        int numeroMin;
        try {
            numeroMin = Integer.parseInt(numeroMinPartecipanti);
            if (numeroMin < 1 || numeroMin > 100)
                throw new IllegalArgumentException("Il numero minimo deve essere positivo e minore di 100.");
            if (numeroMin > numeroMax)
                throw new IllegalArgumentException("Il numero minimo deve essere minore del numero massimo.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il numero minimo di partecipanti non è valido.");
        }

        int[] volontariIds = volontari.stream()
                .mapToInt(Volontario::getId)
                .toArray();

        int[] giorniIds = giorni.stream().mapToInt(Giorno::getId).toArray();

        // Controllo overlap
        if (tipoVisitaDao.siSovrappone(luogoSelezionato.getId(), giorniIds, oraInizio, durataMinuti, dataInizio, dataFine)) {
            throw new IllegalArgumentException("La visita si sovrappone ad un'altra.");
        }

        // Controlli fatti, aggiungere al DB
        try {
            int tipoVisitaId = tipoVisitaDao.aggiungiVisita(titolo, descrizione, dataInizio, dataFine,
                    oraInizio, durataMinuti, entrataLibera, numeroMin,
                    numeroMax, luogoSelezionato, volontariIds, giorniIds, indirizzoPuntoIncontro, comunePuntoIncontro, provinciaPuntoIncontro);
            PuntoIncontro puntoIncontro = new PuntoIncontro(indirizzoPuntoIncontro, comunePuntoIncontro, provinciaPuntoIncontro);
            return new TipoVisita(tipoVisitaId, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numeroMin, numeroMax, luogoSelezionato, puntoIncontro, giorni, volontari);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del tipo di visita", e);
            if ("23000" .equals(e.getSQLState()) && e.getErrorCode() == 1062) {
                throw new DatabaseException("Assicurati che il punto di incontro sia univoco.");
            }
            throw new DatabaseException("Errore nell'inserimento del tipo di visita.");
        }
    }

    public boolean esisteAlmenoUnaVisita() throws DatabaseException {
        try {
            return tipoVisitaDao.esisteAlmenoUnaVisita();
        } catch (SQLException e) {
            throw new DatabaseException("Impossibile verificare l'esistenza di almeno un tipo di visita.");
        }
    }

    public List<String> getTitoliByVolontarioId(int volontarioId) throws DatabaseException {
        try {
            return tipoVisitaDao.getTitoliByVolontarioId(volontarioId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei tipi di visita associati al volontario: ", e);
            throw new DatabaseException("Errore nel recupero dei tipi di visita associati al volontario");
        }
    }

    public List<String> getTitoliByLuogoId(int luogoId) {
        return tipoVisitaDao.getTitoliByLuogoId(luogoId);
    }

    public List<String> getPreviewTipiVisita() throws DatabaseException {
        try {
            return tipoVisitaDao.getPreviewTipiVisita();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei tipi di visita: ", e);
            throw new DatabaseException("Errore durante il recupero dei tipi di visita.");
        }
    }

    public List<String> getPreviewTipiVisita(String luogoNome) throws DatabaseException {
        try {
            return tipoVisitaDao.getPreviewTipiVisita(luogoNome);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei tipi di visita associati al luogo: " + luogoNome, e);
            throw new DatabaseException("Errore durante il recupero dei tipi di visita associati al luogo.");
        }
    }

    public ArrayList<TipoVisita> findByVolontario(int id) throws DatabaseException {
        ArrayList<TipoVisita> visite = new ArrayList<>();

        for (String titolo : getTitoliByVolontarioId(id)) {
            Optional<TipoVisita> tipoVisita = getByTitolo(titolo);
            if (tipoVisita.isEmpty()) {
                throw new DatabaseException("Errore durante il recupero di un tipo di visita");
            }
            visite.add(tipoVisita.get());
        }
        return visite;

    }

    public ArrayList<TipoVisita> findByMese(YearMonth mese) throws DatabaseException {
        ArrayList<TipoVisita> visite = new ArrayList<>();

        for (String titolo : getTitoliByMese(mese)) {
            Optional<TipoVisita> tipoVisita = getByTitolo(titolo);
            if (tipoVisita.isEmpty()) {
                throw new DatabaseException("Errore durante il recupero di un tipo di visita.");
            }
            visite.add(tipoVisita.get());
        }
        return visite;

    }

    public List<String> getTitoliByMese(YearMonth mese) throws DatabaseException {
        try {
            return tipoVisitaDao.getTitoliByMese(mese);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei tipi di visita ricorrenti nel mese " + mese + ": ", e);
            throw new DatabaseException("Errore nel recupero dei tipi di visita ricorrenti nel mese.");
        }
    }

    public Optional<TipoVisita> getByTitolo(String titolo) throws DatabaseException {
        try {
            TipoVisita base = tipoVisitaDao.getByTitolo(titolo);
            if (base == null) {
                return Optional.empty();
            }

            int tipoVisitaId = base.getId();

            Set<Giorno> giorni = giornoService.getByTipoVisitaId(tipoVisitaId);
            Set<Volontario> volontari = volontarioService.getByTipoVisitaId(tipoVisitaId);

            return Optional.of(new TipoVisita(
                    base.getId(),
                    base.getTitolo(),
                    base.getDescrizione(),
                    base.getDataInizio(),
                    base.getDataFine(),
                    base.getOraInizio(),
                    base.getDurataMinuti(),
                    base.getEntrataLibera(),
                    base.getNumMinPartecipanti(),
                    base.getNumMaxPartecipanti(),
                    base.getLuogo(),
                    base.getPuntoIncontro(),
                    giorni,
                    volontari
            ));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupera del tipo di visita", e);
            throw new DatabaseException("Impossibile recuperare il tipo di visita");
        }
    }

    public void setVolontarioService(VolontarioService volontarioService) {
        this.volontarioService = volontarioService;
    }
}
