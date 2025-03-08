package com.unibs;

import com.unibs.models.User;
import com.unibs.models.Tuple;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    protected Tuple<Boolean, User> authenticate(String username, String password) {
        // Check for unregistered users
        // Map<String, User> users = DatabaseFactory.getDatabaseManager().getUnregisteredUsers();
        Map<String, User> users = new HashMap<>();
        users.put("ciao", new User("ciao", "ciao", "CONF"));
        User user = users.get(username);

        if (user != null && user.checkPassword(password)) {
            return new Tuple<Boolean, User>(false, user);
        }

        // Check for registered users
        users = DatabaseFactory.getDatabaseManager().getUsers();
        user = users.get(username);

        if (user != null && user.checkPassword(password)) {
            return new Tuple<Boolean, User>(true, user);
        }

        // user not found
        return new Tuple<Boolean, User>(false, null);
    }

    protected void changePassword(User user, String newPassword) throws DatabaseException {
        String oldPassword = user.getPassword();
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente.");
        }

        user.setPassword(newPassword);

        try {
            DatabaseFactory.getDatabaseManager().changePassword(user.getUsername(), newPassword);
        } catch (DatabaseException e) {
            user.setPassword(oldPassword);
            throw new DatabaseException("Errore nell'aggiornamento della password per " + user.getUsername(), e);
        }

    }

}
