package com.unibs.daos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.unibs.utils.DatabaseException;
import com.unibs.utils.DatabaseManager;
import com.unibs.models.Utente;
import com.unibs.models.Volontario;

public class UtenteDao {

    public Utente findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Utente(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"), rs.getInt("ruolo_id"),
                        rs.getObject("last_login", LocalDate.class));
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return null;
    }

    public void updatePassword(Utente utente) throws DatabaseException {
        String sql = "UPDATE utenti SET password_hash = ?, last_login = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        LocalDate now = LocalDate.now();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)
        ) {
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

    public static int getIdByUsername(String username) {
        String sql = "SELECT id FROM utenti WHERE username = ? AND ruolo_id = 2";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

            return -1;

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la ricerca del volontario per nome: " + e.getMessage());
        }
    }

    public Set<Volontario> findVolontariByTipoVisitaId(int tipoVisitaId) throws SQLException {
        Set<Volontario> volontari = new HashSet<>();
        String query = "SELECT u.id, u.username FROM utenti u JOIN tipi_visita_volontari tvv ON u.id = tvv.volontario_id WHERE tvv.tipo_visita_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tipoVisitaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");

                volontari.add(new Volontario(id, username, null, null, null));
            }
        }

        return volontari;
    }

    public void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate) throws SQLException {
        if (selezionate == null || selezionate.isEmpty()) {
            return;
        }

        LocalDate primaData = selezionate.get(0);
        int mese = primaData.getMonthValue();
        int anno = primaData.getYear();

        String deleteQuery = "DELETE FROM disponibilita WHERE volontario_id = ? AND EXTRACT(MONTH FROM data_disponibile) = ? AND EXTRACT(YEAR FROM data_disponibile) = ?";
        String insertQuery = "INSERT INTO disponibilita (volontario_id, data_disponibile) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                 PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                deleteStmt.setInt(1, volontario.getId());
                deleteStmt.setInt(2, mese);
                deleteStmt.setInt(3, anno);
                deleteStmt.executeUpdate();

                // Inserisce le nuove date
                for (LocalDate data : selezionate) {
                    insertStmt.setInt(1, volontario.getId());
                    insertStmt.setDate(2, Date.valueOf(data));
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
