package com.unibs;

import java.time.LocalDate;

import com.unibs.models.User;

public class LoginService {

    protected User authenticate(String username, String password)
            throws DatabaseException, IllegalArgumentException {
        if (username.isEmpty() || password.isEmpty())
            throw new IllegalArgumentException("Username e password non possono essere vuoti.");

        try {
            User user = UserDao.findByUsername(username);

            if (user != null && user.checkPassword(password)) {
                return user;
            }

        } catch (DatabaseException e) {
            throw e;
        }
        return null;
    }

    protected void registerUser(User user, String newPassword) throws DatabaseException, IllegalArgumentException {
        if (newPassword.isEmpty())
            throw new IllegalArgumentException("La nuova password non può essere vuota");
        String oldPassword = user.getPassword();
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente.");
        }
        user.setPassword(newPassword);

        try {
            UserDao.registerUser(user.getUsername(), newPassword, LocalDate.now());
        } catch (DatabaseException e) {
            user.setPassword(oldPassword);
            throw e;
        }
    }

}
