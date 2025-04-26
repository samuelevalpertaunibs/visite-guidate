package com.unibs.utils;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {

    public static void setupLogger() {
        Logger rootLogger = Logger.getLogger("");

        // Rimuove gli handler di default
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // Crea un formatter leggibile
        Formatter formatter = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new java.util.Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage());
            }
        };

        try {
            FileHandler fileHandler = new FileHandler("app.log", true); // append = true
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(formatter);
            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Errore durante la configurazione del logger: " + e.getMessage());
        }

        // Imposta il livello minimo globale
        rootLogger.setLevel(Level.ALL);
    }
}
