package com.unibs.services;

import com.unibs.daos.TipoVisitaDao;
import com.unibs.models.Giorno;
import com.unibs.models.Luogo;
import com.unibs.models.TipoVisita;
import com.unibs.models.Volontario;
import com.unibs.models.builders.TipoVisitaBuilder;
import com.unibs.utils.DatabaseException;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TipoVisitaService {
    private static final Logger LOGGER = Logger.getLogger(TipoVisitaService.class.getName());

    private final TipoVisitaDao tipoVisitaDao;

    public TipoVisitaService(TipoVisitaDao tipoVisitaDao) {
        this.tipoVisitaDao = tipoVisitaDao;
    }

    public void aggiungiTipoVisita(String titolo, String descrizione, String dataInizioString, String dataFineString, String oraInizioString, String durataMinutiString, boolean entrataLibera, String numeroMinPartecipanti, String numeroMaxPartecipanti, Luogo luogoSelezionato, Set<Volontario> volontari, Set<Giorno> giorni, String indirizzoPuntoIncontro, String comunePuntoIncontro, String provinciaPuntoIncontro) throws DatabaseException, IllegalArgumentException {

        TipoVisita tv = new TipoVisitaBuilder().conTitolo(titolo).conDescrizione(descrizione).conPeriodo(dataInizioString, dataFineString).conOrario(oraInizioString, durataMinutiString).conNumeroPartecipanti(numeroMinPartecipanti, numeroMaxPartecipanti).conEntrataLibera(entrataLibera).conLuogo(luogoSelezionato).conPuntoIncontro(indirizzoPuntoIncontro, comunePuntoIncontro, provinciaPuntoIncontro).neiGiorni(giorni).conVolontari(volontari).build();

        Set<Integer> volontariIds = tv.getVolontari().keySet();
        Set<Integer> giorniIds = tv.getGiorniSettimana().stream().map(Giorno::getId).collect(Collectors.toSet());

        // Controllo overlap
        if (tipoVisitaDao.siSovrappone(tv.getLuogo().getId(), giorniIds, tv.getOraInizio(), tv.getDurataMinuti(), tv.getDataInizio(), tv.getDataFine())) {
            throw new IllegalArgumentException("La visita si sovrappone ad un'altra.");
        }

        // Controlli fatti, aggiungere al DB
        try {
            tipoVisitaDao.aggiungiVisita(tv.getTitolo(), tv.getDescrizione(), tv.getDataInizio(), tv.getDataFine(), tv.getOraInizio(), tv.getDurataMinuti(), tv.isEntrataLibera(), tv.getNumMinPartecipanti(), tv.getNumMaxPartecipanti(), tv.getLuogo().getId(), volontariIds, giorniIds, tv.getPuntoIncontro().getIndirizzo(), tv.getPuntoIncontro().getComune(), tv.getPuntoIncontro().getProvincia());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del tipo di visita", e);
            if ("23000".equals(e.getSQLState()) && e.getErrorCode() == 1062) {
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

    public List<String> getTitoliByLuogoId(int luogoId) throws DatabaseException {
        try {
            return tipoVisitaDao.getTitoliByLuogoId(luogoId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei tipi di visita: ", e);
            throw new DatabaseException("Errore nel recupero dei tipi di visita.");
        }
    }

    public List<java.lang.String> getNomiTipiVisita() throws DatabaseException {
        try {
            return tipoVisitaDao.getNomiTipiVisita(null);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei tipi di visita: ", e);
            throw new DatabaseException("Errore durante il recupero dei tipi di visita.");
        }
    }

    public List<String> getNomiTipiVisita(String luogoNome) throws DatabaseException {
        try {
            return tipoVisitaDao.getNomiTipiVisita(luogoNome);
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

    public Optional<TipoVisita> getByTitolo(java.lang.String titolo) throws DatabaseException {
        try {
            return tipoVisitaDao.getByTitolo(titolo);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupera del tipo di visita", e);
            throw new DatabaseException("Impossibile recuperare il tipo di visita");
        }
    }

    public List<String> getAllTitoli() throws DatabaseException {
        try {
            return tipoVisitaDao.getAllTitoli();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dei tipi di visita", e);
            throw new DatabaseException("Errore nel recupero dei tipi di visita.");
        }
    }

    public Optional<Integer> getIdByNome(String tv) throws DatabaseException {
        try {
            return tipoVisitaDao.getIdByNome(tv);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero del tipo di visita con nome" + tv, e);
            throw new DatabaseException("Impossibile recuperare il tipo di visita.");
        }
    }

    public void applicaRimozioneTipiVisita() throws DatabaseException {
        try {
            tipoVisitaDao.applicaRimozioneTipiVisita();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione dei tipi di visita eliminati", e);
            throw new DatabaseException("Impossibile rimuovere i tipi di visita eliminati.");
        }
    }

    public void inserisciTVDaRimuovere(String titolo) throws DatabaseException {
        try {
            int id = tipoVisitaDao.getIdByNome(titolo).orElseThrow(Exception::new);
            tipoVisitaDao.inserisciTVDaRimuovere(id);
            tipoVisitaDao.termina(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione del tipo di visita.", e);
            throw new DatabaseException("Impossibile rimuovere il tipo di visita.");
        }
    }

    public void rimuoviNonAssociati() throws DatabaseException {
        try {
            List<String> titoli = tipoVisitaDao.getTitoliNonAssociati();
            for (String titolo : titoli) {
                tipoVisitaDao.rimuovi(titolo);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione del tipo di visita.", e);
            throw new DatabaseException("Impossibile cancellare un tipo di visita..");
        }
    }
}
