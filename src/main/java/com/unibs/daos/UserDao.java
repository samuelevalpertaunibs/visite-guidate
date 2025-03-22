package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.User;

public class UserDao {

    public static User findByUsername(String username) throws DatabaseException {
        String sql = "SELECT username, password_hash, salt, ruolo_id, last_login FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"), rs.getInt("ruolo_id"),
                        rs.getObject("last_login", LocalDate.class));
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return null;
    }

    public static void updatePassword(User user) throws DatabaseException {
        String sql = "UPDATE utenti SET password_hash = ?, last_login = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getPasswordHash());
            stmt.setDate(2, java.sql.Date.valueOf(user.getLastLogin()));
            stmt.setString(3, user.getUsername());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("Errore durante l'aggiornamento della password, utente non trovato.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento della password: " + e.getMessage(), e);
        }
    }

    public static int updateLastLogin(String username) throws DatabaseException {
        String sql = "UPDATE utenti SET last_login = ? WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(2, username);
            return stmt.executeUpdate();

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
