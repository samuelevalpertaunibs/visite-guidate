package com.unibs;

import java.net.URI;
import java.net.http.*;

public class DatabaseFactory {
    private static DatabaseManager instance = null;
    private static String API_KEY;
    private static String DB_URL;
    private static String DB_PORT;

    public static DatabaseManager getDatabaseManager() {
        if (instance == null) {
            if (isOnlineAvailable()) {
                instance = new OnlineDatabaseManager("http://" + DB_URL + ":" + DB_PORT + "/data");
            } else {
                instance = new LocalDatabaseManager("db.json");
            }
        }
        return instance;
    }

    private static boolean isOnlineAvailable() {
        try {
            API_KEY = System.getenv("APP_API_KEY");
            DB_URL = System.getenv("DB_URL");
            DB_PORT = System.getenv("DB_PORT");
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + DB_URL +  ":" + DB_PORT + "/health"))
                .header("APP_API_KEY", API_KEY)
                .GET()
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}

