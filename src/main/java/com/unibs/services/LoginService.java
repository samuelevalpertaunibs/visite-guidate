package com.unibs.services;

import com.unibs.daos.UtenteDao;
import com.unibs.models.Utente;
import com.unibs.utils.DatabaseException;
import com.unibs.utils.DateService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public class LoginService {
    UtenteDao utenteDao = new UtenteDao();

    public Utente autentica(String username, String password)
            throws DatabaseException, IllegalArgumentException {
        if (username.isBlank() || password.isBlank())
            throw new IllegalArgumentException("Username e password non possono essere vuoti");

        Utente utente = utenteDao.findByUsername(username);

        if (utente != null) {
            String passwordHash = hashPassword(password, utente.getSalt());
            if (utente.checkPassword(passwordHash)) {
                return utente;
            }
        }

        return null;
    }

    public void updatePassword(Utente utente, String nuovaPassword, String confermaPassword) throws DatabaseException, IllegalArgumentException {

        if (nuovaPassword == null || nuovaPassword.isBlank() || confermaPassword == null || confermaPassword.isBlank())
            throw new IllegalArgumentException("I campi non possono essere vuoti.");

        if (!nuovaPassword.equals(confermaPassword))
            throw new IllegalArgumentException("le password non coincidono.");

        String oldPassword = utente.getPasswordHash();
        byte[] salt = utente.getSalt();
        String hashedNewPassword = hashPassword(nuovaPassword, salt);

        if (oldPassword.equals(hashedNewPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente");
        }

        if (nuovaPassword.length() < 8 ||
                !nuovaPassword.matches(".*[a-z].*") ||
                !nuovaPassword.matches(".*[A-Z].*") ||
                !nuovaPassword.matches(".*\\d.*") ||
                !nuovaPassword.matches(".*[+*@?=)(/&%$£!].*")) {
            throw new IllegalArgumentException("La nuova password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero ed un carattere speciale");
        }

        utente.setPasswordHash(hashedNewPassword);
        utente.setLastLogin(DateService.today());

        try {
            utenteDao.updatePassword(utente);
        } catch (DatabaseException e) {
            // Ripristina il vecchio stato in caso di errore
            utente.setPasswordHash(oldPassword);
            utente.setLastLogin(null);
            throw e;  // Rilancia l'eccezione
        }
    }

    public LocalDate updateLastLogin(Utente utente) throws DatabaseException {
        try {
            String username = utente.getUsername();
            return utenteDao.updateLastLogin(username);
        } catch (Exception e) {
            throw new DatabaseException("Errore durante l'aggiornamento dell'ultimo accesso");
        }
    }

    private String hashPassword(String password, byte[] salt) {
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
