package com.unibs.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateService {
    private static final Logger LOGGER = Logger.getLogger(DateService.class.getName());

    private static final String SERVER_URL = "http://localhost:3000/api/datetime";

    public static LocalDate today() {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            // L'unica riga è la data nel formato "YYYY-MM-DD"
            String dateString = reader.readLine();
            reader.close();

            return LocalDate.parse(dateString);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            return LocalDate.now(); // fallback
        }
    }
}