package com.unibs.utils;

import java.io.IOException;
import java.util.Date;
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
            public synchronized String format(LogRecord record) {
                String baseMessage = String.format(format,
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage());

                if (record.getThrown() != null) {
                    StringBuilder sb = new StringBuilder(baseMessage);
                    Throwable thrown = record.getThrown();
                    sb.append(thrown.toString()).append("\n");
                    for (StackTraceElement elem : thrown.getStackTrace()) {
                        sb.append("\tat ").append(elem.toString()).append("\n");
                    }
                    return sb.toString();
                } else {
                    return baseMessage;
                }
            }
        };

        try {
            FileHandler fileHandler = new FileHandler("app.log", false);
            fileHandler.setLevel(Level.CONFIG);
            fileHandler.setFormatter(formatter);
            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Errore durante la configurazione del logger: " + e.getMessage());
        }

        rootLogger.setLevel(Level.ALL);
    }
}
