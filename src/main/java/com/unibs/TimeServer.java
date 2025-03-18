package com.unibs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class TimeServer {
    private final String serverAddress;

    //possibile estensioni future
    public TimeServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    //quello corretto
    public TimeServer() {
        // Usa un indirizzo fisso invece del parametro
        this.serverAddress = "http://80.116.92.149:3000";
    }

    //Metodo privato che effettua la connessione al server e ottiene la data e l'ora come stringa.
    private String fetchDateTimeFromServer() throws IOException {
        URL url = new URL(serverAddress);
        HttpURLConnection connection = null;
        Scanner scanner = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);  // Timeout di 5 secondi
            connection.setReadTimeout(5000);     // Timeout di lettura di 5 secondi

            // Controlla il codice di risposta
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Errore nella connessione al TimeServer. Codice di risposta: " + responseCode);
            }
            // Legge la risposta dal server
            scanner = new Scanner(connection.getInputStream(), "UTF-8");
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }finally {
            // Chiude le risorse in modo sicuro
            if (scanner != null) {
                scanner.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    //Ottiene la data corrente dal server esterno
    public LocalDate getCurrentDate() throws IOException {
        String dateTimeString = fetchDateTimeFromServer();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            return dateTime.toLocalDate();
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Formato data non valido dal server: " + dateTimeString,
                    dateTimeString, e.getErrorIndex());
        }
    }

     //Verifica se una data è il 15 del mese.
    public boolean isDay15OfMonth() throws IOException {
        LocalDate currentDate = getCurrentDate();
        return currentDate.getDayOfMonth() == 15;
    }


     //Verifica se una data è il 16 del mese.
    public boolean isDay16OfMonth() throws IOException {
        LocalDate currentDate = getCurrentDate();
        return currentDate.getDayOfMonth() == 16;
    }


    //Verifica se una data NON è il 15 o 16 del mese.
    public boolean isNotDay15Or16OfMonth() throws IOException {
        LocalDate currentDate = getCurrentDate();
        int dayOfMonth = currentDate.getDayOfMonth();
        return dayOfMonth != 15 && dayOfMonth != 16;
    }

    //Verifica se una data È il 15 o 16 del mese.
    public boolean isDay15Or16OfMonth() throws IOException {
        return !isNotDay15Or16OfMonth();
    }
}