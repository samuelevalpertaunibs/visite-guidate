package com.unibs;

import java.net.URI;
import java.net.http.*;
import java.util.Map;

import com.unibs.models.User;

public class OnlineDatabaseManager extends DatabaseManager {

    private static final String API_KEY = System.getenv("APP_API_KEY");
    private static final HttpClient client = HttpClient.newHttpClient();

    public OnlineDatabaseManager(String apiUrl) {
        super(apiUrl);
    }

    @Override
    public String readData() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(databaseSource))
                .header("APP_API_KEY", API_KEY)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 ? response.body() : null;

        } catch (Exception e) {
            System.err.println("Errore nella lettura dal database online: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void writeData(String data) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(databaseSource))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Errore nella scrittura del database online: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Errore nella scrittura del database online: " + e.getMessage());
        }
    }

	@Override
	public Map<String, User> getUnregisteredUsers() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getUnregisteredUsers'");
	}

	@Override
	public Map<String, User> getUsers() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getUsers'");
	}

	@Override
	protected void changePassword(String username, String newPassword) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
	}

}

