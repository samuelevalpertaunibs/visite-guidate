package com.unibs.services;

import com.unibs.daos.DatePrecluseDao;
import com.unibs.utils.DatabaseException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatePrecluseService {
    private static final Logger LOGGER = Logger.getLogger(DatePrecluseService.class.getName());
    private final DatePrecluseDao datePrecluseDao = new DatePrecluseDao();
    private final ConfigService configService;

    public DatePrecluseService(ConfigService configService) {
        this.configService = configService;
    }

    public void aggiungiDataPreclusa(LocalDate dataInserita) throws IllegalArgumentException, DatabaseException {
        if (dataInserita == null ) {
            throw new IllegalArgumentException("Inserisci una data.");
        }

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

    public LocalDate getPrimaDataPrecludibile() {
        return configService.getConfig().getPeriodoCorrente().plusMonths(3).withDayOfMonth(1);
    }

}
