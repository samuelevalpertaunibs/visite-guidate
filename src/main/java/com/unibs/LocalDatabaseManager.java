package com.unibs;
import java.nio.file.*;
import java.util.Map;

import com.unibs.models.User;

import java.io.*;

public class LocalDatabaseManager extends DatabaseManager {

    public LocalDatabaseManager(String filePath) {
        super(filePath);
    }

    @Override
    public String readData() {
        try {
            return Files.readString(Path.of(databaseSource));
        } catch (IOException e) {
            System.err.println("Errore nella lettura del database locale: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void writeData(String data) {
        try {
            Files.writeString(Path.of(databaseSource), data);
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del database locale: " + e.getMessage());
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

