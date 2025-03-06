package com.unibs;

import com.unibs.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    protected User authenticate(String username, String password) {
//        Map<String, User> users = DatabaseManager.getAllUsers();
        Map<String, User> users = new HashMap<>();
        users.put("ciao", new User("ciao", "ciao", "CONFA"));
        User user = users.get(username);

        if (user != null && user.checkPassword(password)) {
            return user;
        }

        return null;
    }

}
