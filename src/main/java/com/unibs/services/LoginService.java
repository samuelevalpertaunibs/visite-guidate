package com.unibs.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.unibs.DatabaseException;
import com.unibs.daos.UserDao;
import com.unibs.models.User;

public class LoginService {

    public User authenticate(String username, String password)
            throws DatabaseException, IllegalArgumentException {
        if (username.isBlank() || password.isBlank())
            throw new IllegalArgumentException("Username e password non possono essere vuoti.");

        User user = UserDao.findByUsername(username);

        if (user != null) {
            String passwordHash = hashPassword(password, user.getSalt());
            if (user.checkPassword(passwordHash)) {
                return user;
            }
        }

        return null;
    }

    public void updatePassword(User user, String newPassword) throws DatabaseException, IllegalArgumentException {
        String oldPassword = user.getPasswordHash();
        byte[] salt = user.getSalt();
        String hashedNewPassword = hashPassword(newPassword, salt);

        if (oldPassword.equals(hashedNewPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente.");
        }
        user.setPasswordHash(hashedNewPassword);
        user.setLastLogin(LocalDate.now());

        try {
            UserDao.updatePassword(user);
        } catch (DatabaseException e) {
            // Ripristina il vecchio stato in caso di errore
            user.setPasswordHash(oldPassword);
            user.setLastLogin(null);
            throw e;  // Rilancia l'eccezione
        }
    }

    public User updateLastLogin(User user) throws DatabaseException {
        String username = user.getUsername();
        int updated = UserDao.updateLastLogin(username);
        if (updated > 0) {
            return UserDao.findByUsername(username);
        }
        return null;
    }

    public String hashPassword(String password, byte[] salt) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashedPassword);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
