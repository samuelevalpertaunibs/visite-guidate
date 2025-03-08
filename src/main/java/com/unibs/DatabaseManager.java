package com.unibs;

import java.util.Map;

import com.unibs.models.User;

public abstract class DatabaseManager {
    protected String databaseSource;

    protected DatabaseManager(String databaseSource) {
        this.databaseSource = databaseSource;
    }

    public abstract String readData();

    public abstract void writeData(String data);

    public abstract Map<String, User> getUnregisteredUsers();

    public abstract Map<String, User> getUsers();

	protected abstract void changePassword(String username, String newPassword);
    
}
