package com.unibs.services;

import com.unibs.daos.UtenteDao;
import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VolontarioService {
    private static final Logger LOGGER = Logger.getLogger(VolontarioService.class.getName());
    private static final String DEFAULT_PASSWORD = "password";
    final UtenteDao utenteDao = new UtenteDao();

    public List<Volontario> findAllVolontari() throws DatabaseException {
        try {
            return utenteDao.getAllVolontari();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta della data preclusa", e);
            throw new DatabaseException("Errore nel recupero dei volontari");
        }
    }

    public void rimuoviNonAssociati() {
        try {
            List<Integer> volontariNonAssociati = utenteDao.getIdVolontariNonAssociati();
            for (Integer id : volontariNonAssociati) {
                utenteDao.rimuovi(id);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante la rimozione dei volontari non associati", e);
            throw new DatabaseException("Impossibile rimuovere i volontari non associati ad alcuna visita.");
        }
    }

    public void aggiungiVolontario(String username) {
        if (utenteDao.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username già esistente.");
        }

        try {
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(DEFAULT_PASSWORD, salt);

            Utente nuovoVolontario = new Utente(0, username, hashedPassword, salt, 2, null);
            utenteDao.inserisciUtente(nuovoVolontario);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'aggiunta del volontario.", e);
            throw new DatabaseException("Impossibile aggiungere il volontario.");
        }
    }

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
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
