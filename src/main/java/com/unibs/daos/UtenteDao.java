package com.unibs.daos;

import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;
import com.unibs.utils.DatabaseManager;
import com.unibs.utils.DateService;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class UtenteDao {

    public Utente findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Utente(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"), rs.getInt("ruolo_id"), rs.getObject("last_login", LocalDate.class));
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return null;
    }

    public void updatePassword(Utente utente) throws DatabaseException {
        String sql = "UPDATE utenti SET password_hash = ?, last_login = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, utente.getPasswordHash());
            stmt.setDate(2, java.sql.Date.valueOf(utente.getLastLogin()));
            stmt.setString(3, utente.getUsername());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("Errore durante l'aggiornamento della password, utente non trovato.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento della password: " + e.getMessage(), e);
        }
    }

    public LocalDate updateLastLogin(String username) throws SQLException {
        String sql = "UPDATE utenti SET last_login = ? WHERE username = ?";
        LocalDate now = DateService.today();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(now));
            stmt.setString(2, username);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Nessun utente aggiornato per username.");
            }

            return now;
        }
    }

    public List<Volontario> getAllVolontari() throws SQLException {
        ArrayList<Volontario> volontari = new ArrayList<>();
        String query = "SELECT * FROM utenti WHERE ruolo_id = 2";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                byte[] salt = rs.getBytes("salt");
                LocalDate lastLogin = rs.getObject("last_login", LocalDate.class);
                volontari.add(new Volontario(id, username, passwordHash, salt, lastLogin));
            }
        }
        return volontari;
    }

    public void rimuovi(int id) throws SQLException {
        String sql = "DELETE FROM utenti WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Integer> getIdVolontariNonAssociati() throws SQLException {
        String sql = "SELECT id FROM utenti WHERE ruolo_id = 2 AND id NOT IN (SELECT volontario_id FROM tipi_visita_volontari)";
        List<Integer> idVolontari = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                idVolontari.add(rs.getInt("id"));
            }
        }

        return idVolontari;
    }

    public void inserisciUtente(Utente nuovoUtente) throws SQLException {
        String sql = "INSERT INTO utenti VALUES (DEFAULT, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuovoUtente.getUsername());
            stmt.setString(2, nuovoUtente.getPasswordHash());
            stmt.setBytes(3, nuovoUtente.getSalt());
            stmt.setInt(4, nuovoUtente.getRole());
            if ((nuovoUtente.getLastLogin() == null)) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, Date.valueOf(nuovoUtente.getLastLogin()));
            }

            stmt.executeUpdate();
        }
    }

}
