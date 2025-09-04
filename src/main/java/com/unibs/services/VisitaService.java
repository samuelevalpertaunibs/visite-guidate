package com.unibs.services;

import com.unibs.daos.VisitaDao;
import com.unibs.models.*;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisitaService {
    private static final Logger LOGGER = Logger.getLogger(VisitaService.class.getName());
    private final VisitaDao visitaDao = new VisitaDao();
    private final TipoVisitaService tipoVisitaService;
    private final ConfigService configService;
    private final VolontarioService volontarioService;
    private final GiornoService giornoService;

    public VisitaService(TipoVisitaService tipoVisitaService, ConfigService configService, VolontarioService volontarioService, GiornoService giornoService) {
        this.tipoVisitaService = tipoVisitaService;
        this.configService = configService;
        this.volontarioService = volontarioService;
        this.giornoService = giornoService;
    }

    public List<Visita> getVisitePreviewByStato(Visita.StatoVisita stato) throws DatabaseException {
        try {
            return visitaDao.getVisitePreview(stato);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite", e);
            throw new DatabaseException("Impossibile recuperare le visite.");
        }
    }

    public List<Visita> getVisitePreviewByFruitore(Visita.StatoVisita stato, java.lang.String nomeFruitore) throws DatabaseException {
        try {
            return visitaDao.getVisitePreviewByFruitore(stato, nomeFruitore);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite", e);
            throw new DatabaseException("Impossibile recuperare le visite.");
        }
    }

    public List<Visita> getVisitePreviewByVolontario(Visita.StatoVisita stato, Volontario volontario) throws DatabaseException {
        try {
            return visitaDao.getVisitePreviewByVolontario(stato, volontario.getUsername());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite", e);
            throw new DatabaseException("Impossibile recuperare le visite.");
        }
    }

    public void creaPiano() throws DatabaseException {
        YearMonth meseTarget = configService.getMesePeriodoCorrente().plusMonths(2);
        Set<TipoVisita> tipiVisite = new HashSet<>(tipoVisitaService.findByMese(meseTarget));
        Map<Integer, Set<Visita>> visitePerVolontario = new HashMap<>();
        Map<Integer, Set<LocalDate>> dateOccupateVolontario = new HashMap<>();
        Map<TipoVisita, Set<LocalDate>> dateOccupateTipoVisita = new HashMap<>();

        for (TipoVisita tipoVisita : tipiVisite) {
            for (Volontario volontario : tipoVisita.getVolontari()) {
                int volontarioId = volontario.getId();
                List<LocalDate> disponibilita = volontarioService.getDateDisponibiliByMese(volontario, meseTarget);

                dateOccupateVolontario.putIfAbsent(volontarioId, new HashSet<>());
                dateOccupateTipoVisita.putIfAbsent(tipoVisita, new HashSet<>());

                for (LocalDate data : disponibilita) {
                    Giorno giornoSettimana = giornoService.fromDayOfWeek(data.getDayOfWeek());

                    // Skip se il giorno non è valido per il tipoVisita
                    if (!tipoVisita.getGiorniSettimana().contains(giornoSettimana)) continue;

                    Set<LocalDate> occupateTipo = dateOccupateTipoVisita.get(tipoVisita);
                    Set<LocalDate> occupateVolontario = dateOccupateVolontario.get(volontarioId);

                    // Skip se la data è già occupata da questo tipoVisita o da questo volontario
                    if (occupateTipo.contains(data) || occupateVolontario.contains(data)) continue;

                    // Assegna la visita
                    Visita visita = new Visita(null, tipoVisita, data, volontario);
                    visitePerVolontario.computeIfAbsent(volontarioId, k -> new HashSet<>()).add(visita);

                    occupateTipo.add(data);
                    occupateVolontario.add(data);
                }
            }
        }

        try {
            // Chiudo la raccolta delle disponibilità
            configService.chiudiRaccoltaDisponibilita();

            List<Visita> tutteLeVisite = visitePerVolontario.values()
                    .stream() // Stream<Set<Visita>>
                    .flatMap(Set::stream) // Stream<Visita>
                    .toList(); // List<Visita>

            visitaDao.inserisciVisite(tutteLeVisite);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'inserimento delle visite: ", e);
            throw new DatabaseException("Impossibile inserire il piano delle visite.");
        }
    }

    public void rimuoviVecchieDisponibilita() throws DatabaseException {
        try {
            visitaDao.rimuoviDisponibilita();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione delle vecchie disponibilità: ", e);
            throw new DatabaseException("Errore durante la rimozione delle disponibilità.");
        }

    }

    public int getIscrizioniRimanentiById(Integer id) {
        try {
            return visitaDao.getIscrizioniRimanentiById(id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il calcolo dei posti disponibili per visita con id: " + id, e);
            throw new DatabaseException("Errore durante il calcolo dei posti disponibili.");
        }
    }

    public List<java.lang.String> getCodiciPrenotazioneFruitorePerVista(java.lang.String nomeFruitore, Integer idVisita) {
        try {
            return visitaDao.getCodiciPrenotazioneFruitorePerVista(nomeFruitore, idVisita);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle prenotazioni per: " + nomeFruitore, e);
            throw new DatabaseException("Errore durante il recupero delle prenotazioni.");
        }
    }

    public void disdici(Fruitore fruitore, Integer codiceIscrizione) {
        try {
            if (!fruitore.getId().equals(visitaDao.getIdFruitoreByIdIscrizione(codiceIscrizione))) {
                throw new IllegalArgumentException("Nessuna iscrizione trovata con questo codice per il tuo utente");
            }

            Integer visitaId = visitaDao.getIdVisitaByIdIscrizione(codiceIscrizione);
            visitaDao.disdiciIscrizione(codiceIscrizione);

            // Se la visita era al completo, dopo la disdetta torna proposta
            if (visitaDao.getStatoById(visitaId).get() == Visita.StatoVisita.COMPLETA) {
                visitaDao.setStatoById(visitaId, Visita.StatoVisita.PROPOSTA);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero della prenotazione.", e);
            throw new DatabaseException("Impossibile recuperare l'iscrizione.");
        }
    }

    public List<java.lang.String> getCodiciPrenotazionePerVista(Volontario volontario, Integer id) throws DatabaseException {
        try {
            return visitaDao.getCodiciPrenotazionePerVista(volontario.getUsername(), id);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle prenotazioni per: " + volontario.getUsername(), e);
            throw new DatabaseException("Errore durante il recupero delle prenotazioni.");
        }
    }

    public void chiudiIscrizioneVisiteComplete() throws DatabaseException {
        try {
            List<Integer> idVisiteComplete = visitaDao.getIdVisiteCompleteDaChiudere();
            for (Integer idVisita : idVisiteComplete) {
                visitaDao.setStatoById(idVisita, Visita.StatoVisita.CONFERMATA);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la chiudura delle iscrizioni di visite complete.", e);
            throw new DatabaseException("Errore durante la chiusura delle iscrizioni di visite complete.");
        }
    }

    public void chiudiIscrizioneVisiteDaFare() throws DatabaseException {
        try {
            List<Integer> idVisiteDaFare = visitaDao.getIdVisiteDaFare();
            for (Integer idVisita : idVisiteDaFare) {
                visitaDao.setStatoById(idVisita, Visita.StatoVisita.CONFERMATA);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la chiudura delle iscrizioni di visite proposte con minimo iscritti raggiunto.", e);
            throw new DatabaseException("Errore durante la chiusura delle iscrizioni di visite proposte.");
        }
    }

    public void chiudiIscrizioneVisitaDaCancellare() throws DatabaseException {
        try {
            List<Integer> idVisiteDaCancellare = visitaDao.getIdVisiteDaCancellare();
            for (Integer idVisita : idVisiteDaCancellare) {
                visitaDao.setStatoById(idVisita, Visita.StatoVisita.CANCELLATA);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la chiudura delle iscrizioni di visite proposte con minimo iscritti non raggiunto.", e);
            throw new DatabaseException("Errore durante la chiusura delle iscrizioni di visite proposte.");
        }
    }

    public void generaVisiteEffettuate() {
        try {
            List<Integer> idVisiteDaRendereEffettuate = visitaDao.getIdVisiteDaRendereEffettuate();
            for (Integer idVisita : idVisiteDaRendereEffettuate) {
                visitaDao.archiviaVisita(idVisita);
                visitaDao.rimuoviById(idVisita);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante  la generazione delle visite effettuate.", e);
            throw new DatabaseException("Errore durante la generazione delle visite effettuate.");
        }
    }

    public void rimuoviVisiteCancellate() throws DatabaseException {
        try {
            visitaDao.rimuoviVisiteCancellate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione delle visite cancellate.", e);
            throw new DatabaseException("Errore durante la rimozione delle visite cancellate.");
        }
    }

    public List<Visita> getVisiteFromArchivio() throws DatabaseException {
        try {
            return visitaDao.getVisiteFromArchivio();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero delle visite nell'archivio.", e);
            throw new DatabaseException("Errore durante il recupero delle visite nell'archivio.");
        }
    }
}
