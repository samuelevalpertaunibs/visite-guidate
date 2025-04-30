package com.unibs.services;

import com.unibs.utils.DatabaseException;
import com.unibs.daos.DatePrecluseDao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatePrecluseService {
    private static final Logger LOGGER = Logger.getLogger(DatePrecluseService.class.getName());
    private final DatePrecluseDao datePrecluseDao = new DatePrecluseDao();
    private final ConfigService configService;

    public DatePrecluseService(ConfigService configService) {
        this.configService = configService;
    }

    public void aggiungiDataPreclusa(String dataString) throws IllegalArgumentException, DatabaseException {
        if (dataString == null || dataString.isEmpty()) {
            throw new IllegalArgumentException("Inserisci una data.");
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataInserita = getDataInserita(dataString, dateFormatter);

        try {
            // Controllo se la data è già preclusa prima di inserirla
            if (datePrecluseDao.isDataPreclusa(dataInserita)) {
                throw new IllegalArgumentException("Errore: La data è già preclusa.");
            }
            datePrecluseDao.aggiungiDataPreclusa(dataInserita);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta della data preclusa", e);
            throw new DatabaseException("Impossibile precludere la data.");
        }
    }

    private LocalDate getDataInserita(String giorno, DateTimeFormatter dateFormatter) {
        int giornoInt;
        try {
            giornoInt = Integer.parseInt(giorno);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato non valido.");
        }

        LocalDate primoGiornoMesePrecluso = getPrimaDataPrecludibile();

        LocalDate dataInserita;
        try {
            String dataCompleta = String.format("%02d/%02d/%d", giornoInt, primoGiornoMesePrecluso.getMonthValue(), primoGiornoMesePrecluso.getYear());
            dataInserita = LocalDate.parse(dataCompleta, dateFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Il formato della data di inizio non è corretto.");
        }
        return dataInserita;
    }

    public LocalDate getPrimaDataPrecludibile() {
        return configService.getConfig().getPeriodoCorrente().plusMonths(3).withDayOfMonth(1);
    }

    public Set<LocalDate> findByMonth(YearMonth mese) {
        try {
            return datePrecluseDao.findByMonth(mese);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la ricerca delle date precluse", e);
            throw new DatabaseException("Impossibile recuperare le date precluse.");
        }
    }
}
