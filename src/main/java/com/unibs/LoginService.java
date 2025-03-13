package com.unibs;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.unibs.models.User;

public class LoginService {

    protected User authenticate(String username, String password)
            throws DatabaseException, IllegalArgumentException {
        if (username.isEmpty() || password.isEmpty())
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

    protected void registerUser(User user, String newPassword) throws DatabaseException, IllegalArgumentException {
        if (newPassword.isEmpty())
            throw new IllegalArgumentException("La nuova password non può essere vuota.");
        String oldPassword = user.getPasswordHash();

        byte [] salt = user.getSalt();
        String hashedNewPassword = hashPassword(newPassword,salt);

        if (oldPassword.equals(hashedNewPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente.");
        }
        user.setPasswordHash(hashedNewPassword);

        try {
            UserDao.registerUser(user.getUsername(), hashedNewPassword, LocalDate.now());
        } catch (DatabaseException e) {
            user.setPasswordHash(oldPassword);
            throw e;
        }
    }

    protected User updateLastLogin(String username) throws DatabaseException {

        int updated = UserDao.updateLastLogin(username);
        if (updated > 0) {
            return UserDao.findByUsername(username);
        }

        return null;
    }
    public String hashPassword(String password, byte[] salt) {
        MessageDigest md = null;
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
