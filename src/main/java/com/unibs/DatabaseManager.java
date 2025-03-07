package com.unibs;

import com.unibs.models.User;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

    public static Map<String, User> getUnregisteredUsers() {
        return new HashMap<String, User>();
    }

    public static Map<String, User> getUsers() {
        return new HashMap<String, User>();
    }

	public static void changePassword(String username, String newPassword) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
	}
}
