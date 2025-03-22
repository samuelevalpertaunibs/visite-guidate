package com.unibs.services;

import com.unibs.daos.DatePrecluseDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatePrecluseService {
    public void aggiungiDataPrecluse(String giorno) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (giorno == null || giorno.isEmpty()) {
            throw new IllegalArgumentException("Inserisci una data");
        }

        int giornoInt;
        try{
           giornoInt = Integer.parseInt(giorno);
        }catch (Exception e){
            throw new IllegalArgumentException("Formato non valido");
        }

        LocalDateTime primoGiornoMesePrecluso = LocalDateTime.now().plusMonths(3).withDayOfMonth(1);
        boolean esistonoDate = DatePrecluseDao.esistonoDatePrecluseNelMese(primoGiornoMesePrecluso.getYear(),primoGiornoMesePrecluso.getMonthValue());
        LocalDate dataInserita;
        try {
            String dataCompleta = String.format("%02d/%02d/%d", giornoInt, primoGiornoMesePrecluso.getMonthValue(), primoGiornoMesePrecluso.getYear());
            dataInserita = LocalDate.parse(dataCompleta, dateFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Il formato della data di inizio non è corretto");
        }
        // Controllo se la data è già preclusa prima di inserirla
        if (DatePrecluseDao.isDataPreclusa(dataInserita)) {
            throw new IllegalArgumentException("Errore: La data è già preclusa.");
        }
        DatePrecluseDao.aggiungiDataPreclusa(dataInserita);
    }
}
