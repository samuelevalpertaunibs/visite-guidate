package com.unibs.services;

import com.unibs.daos.VisitaDao;
import com.unibs.models.Giorno;
import com.unibs.models.TipoVisita;
import com.unibs.models.Visita;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisitaService {
    private final VisitaDao visitaDao = new VisitaDao();
    private final TipoVisitaService tipoVisitaService;
    private final ConfigService configService;
    private final VolontarioService volontarioService;
    private final GiornoService giornoService;
    private static final Logger LOGGER = Logger.getLogger(VisitaService.class.getName());

    public VisitaService(TipoVisitaService tipoVisitaService, ConfigService configService, VolontarioService volontarioService, GiornoService giornoService) {
        this.tipoVisitaService = tipoVisitaService;
        this.configService = configService;
        this.volontarioService = volontarioService;
        this.giornoService = giornoService;
    }

    public List<Visita> getVisitePreview(Visita.StatoVisita stato) throws DatabaseException {
        try {
            return visitaDao.getVisitePreview(stato);
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
                    if (!tipoVisita.getGiorni().contains(giornoSettimana)) continue;

                    Set<LocalDate> occupateTipo = dateOccupateTipoVisita.get(tipoVisita);
                    Set<LocalDate> occupateVolontario = dateOccupateVolontario.get(volontarioId);

                    // Skip se la data è già occupata da questo tipoVisita o da questo volontario
                    if (occupateTipo.contains(data) || occupateVolontario.contains(data)) continue;

                    // Assegna la visita
                    Visita visita = new Visita(tipoVisita, data, volontario);
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
}
