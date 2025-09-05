package com.unibs.services;

import com.unibs.daos.ComuneDao;
import com.unibs.daos.ConfigDao;
import com.unibs.mappers.ComuneMapper;
import com.unibs.mappers.ConfigMapper;
import com.unibs.models.Comune;
import com.unibs.models.Config;
import com.unibs.utils.DatabaseException;
import com.unibs.utils.DateService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConfigService
 */
public class ConfigService {
    private final ConfigDao configDao;
    private final ComuneDao comuneDao;
    private final Logger LOGGER = Logger.getLogger(ConfigService.class.getName());

    public ConfigService() {
        this.configDao = new ConfigDao(new ConfigMapper());
        this.comuneDao = new ComuneDao(new ComuneMapper());
    }

    public Config getConfig() throws DatabaseException {
        try {
            Config config = configDao.getConfig()
                    .orElseThrow(() -> new DatabaseException("Configurazione non trovata."));

            List<Comune> ambitoTerritoriale = comuneDao.findAll();
            config.setAmbitoTerritoriale(ambitoTerritoriale);

            return config;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante il recupero dell'ambito territoriale", e);
            throw new DatabaseException("Ambito territoriale non trovato.");
        }
    }

    public void aggiungiComune(String nome, String provincia, String regione) throws DatabaseException, IllegalArgumentException {
        if (nome.isBlank() || provincia.isBlank() || regione.isBlank())
            throw new IllegalArgumentException("I campi non possono essere vuoti.");

        Config config = getConfig();
        Comune comuneDaAggiungere = new Comune(0, nome, provincia, regione);

        if (config.doesInclude(comuneDaAggiungere))
            throw new IllegalArgumentException("Il comune è gia presente.");

        try {
            comuneDao.aggiungiComune(comuneDaAggiungere);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del comune", e);
            throw new DatabaseException("Impossibile inserire il comune.");

        }
    }

    public void initDefault() throws DatabaseException {
        try {
            configDao.initDefault();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'inizializzazione delle configurazioni di default", e);
            throw new DatabaseException("Errore durante l'inizializzazione della configurazione.");
        }
    }

    public boolean isInitialized() {
        try {
            Config config = getConfig();
            return config.getPeriodoCorrente() != null;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public int setNumeroMaxPersone(String numeroMaxPersone) throws DatabaseException {
        try {
            int numeroMax = Integer.parseInt(numeroMaxPersone);
            if (numeroMax < 1 || numeroMax > 100)
                throw new IllegalArgumentException("Il numero max deve essere positivo e minore di 100.");
            configDao.setNumeroMax(numeroMax);
            return numeroMax;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Il numero massimo di persone non è valido.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la modifica della configurazione", e);
            throw new DatabaseException("Errore durante la modifica del numero massimo di persone.");
        }

    }

    public boolean esisteAlmenoUnComune() {
        try {
            Config config = getConfig();
            return config.getNumeroComuni() > 0;
        } catch (DatabaseException e) {
            return false;
        }
    }

    public void setPeriodoCorrente(LocalDate periodoCorrente) throws DatabaseException {
        try {
            configDao.setPeriodoCorrente(periodoCorrente);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la modifica della configurazione", e);
            throw new DatabaseException("Errore durante la modifica della configurazione.");
        }
    }

    public boolean regimeAttivo() {
        try {
            LocalDate periodoCorrente = getConfig().getPeriodoCorrente();
            if (periodoCorrente == null)
                return false;

            // true -> oggi >= inizioPeriodoCorrente
            return !DateService.today().isBefore(periodoCorrente);
        } catch (DatabaseException e) {
            return false;
        }
    }

    public List<Comune> getAmbitoTerritoriale() throws DatabaseException {
        return getConfig().getAmbitoTerritoriale();
    }

    public int getNumeroMax() {
        return getConfig().getNumeroMassimoIscrizioniPrenotazione();
    }

    public YearMonth getMesePeriodoCorrente() throws DatabaseException {
        return YearMonth.from(getConfig().getPeriodoCorrente());
    }

    // Nel database utilizzo la dataPeriodoCorrente per indicare oltre che il periodo corrente anche la momentanea apertura
    // alla raccolta delle disponibilità dei volontari
    // Imposto il giorno della dataPeriodoCorrente al 28 mentre la raccolta delle disponibilità è chiusa, altrimenti
    // sarà il 16 del mese del periodo di attività.
    public boolean isRaccoltaDisponibilitaChiusa() {
        try {
            return getConfig().getPeriodoCorrente().getDayOfMonth() == 28;
        } catch (Exception e) {
            return true;
        }
    }

    // Se oggi è almeno il 16 del mese successivo a quello attivo la creazione è possibile
    public boolean isCreazioneNuovoPianoPossibile() {
        YearMonth periodoCorrente = getMesePeriodoCorrente();
        return DateService.today().isAfter(periodoCorrente.plusMonths(1).atDay(15));
    }

    // Utilizzo la dataPeriodoCorrente per distinguere un periodo di raccolta disponibilita attiva da uno non
    // RACCOLTA ATTIVA -> dataPeriodoCorrente ha giorno del mese 16
    // RACCOLTA DISATTIVA -> dataPeriodoCorrente ha giorno del mese 28
    public void chiudiRaccoltaDisponibilita() throws DatabaseException {
        try {
            LocalDate dataInizioPeriodoCorrente = getConfig().getPeriodoCorrente();
            configDao.setPeriodoCorrente(dataInizioPeriodoCorrente.withDayOfMonth(28));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la chiusura della raccolta disponibilità", e);
            throw new DatabaseException("Impossibile chiudere la raccolta delle disponibilità.");
        }
    }

    public void riapriRaccoltaDisponibilita() throws DatabaseException {
        try {
            LocalDate dataInizioPeriodoCorrente = getConfig().getPeriodoCorrente();
            configDao.setPeriodoCorrente(dataInizioPeriodoCorrente.withDayOfMonth(16).plusMonths(1));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la riapertura della raccolta disponibilità", e);
            throw new DatabaseException("Impossibile riaprire la raccolta delle disponibilità.");
        }
    }
}
