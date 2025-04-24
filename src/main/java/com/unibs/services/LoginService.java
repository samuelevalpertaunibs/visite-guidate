package com.unibs.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.unibs.DatabaseException;
import com.unibs.daos.UtenteDao;
import com.unibs.models.Utente;

public class LoginService {

    public Utente authenticate(String username, String password)
            throws DatabaseException, IllegalArgumentException {
        if (username.isBlank() || password.isBlank())
            throw new IllegalArgumentException("Username e password non possono essere vuoti");

        Utente utente = UtenteDao.findByUsername(username);

        if (utente != null) {
            String passwordHash = hashPassword(password, utente.getSalt());
            if (utente.checkPassword(passwordHash)) {
                return utente;
            }
        }

        return null;
    }

    public void updatePassword(Utente utente, String newPassword) throws DatabaseException, IllegalArgumentException {
        String oldPassword = utente.getPasswordHash();
        byte[] salt = utente.getSalt();
        String hashedNewPassword = hashPassword(newPassword, salt);

        if (oldPassword.equals(hashedNewPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente");
        }

        if (newPassword.length() < 8 ||
                !newPassword.matches(".*[a-z].*") ||
                !newPassword.matches(".*[A-Z].*") ||
                !newPassword.matches(".*\\d.*") ||
                !newPassword.matches(".*[+*@?=)(/&%$£!].*")) {
            throw new IllegalArgumentException("La nuova password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero ed un carattere speciale");
        }

        utente.setPasswordHash(hashedNewPassword);
        utente.setLastLogin(LocalDate.now());

        try {
            UtenteDao.updatePassword(utente);
        } catch (DatabaseException e) {
            // Ripristina il vecchio stato in caso di errore
            utente.setPasswordHash(oldPassword);
            utente.setLastLogin(null);
            throw e;  // Rilancia l'eccezione
        }
    }

    public Utente updateLastLogin(Utente utente) throws DatabaseException {
        String username = utente.getUsername();
        int updated = UtenteDao.updateLastLogin(username);
        if (updated > 0) {
            return UtenteDao.findByUsername(username);
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
