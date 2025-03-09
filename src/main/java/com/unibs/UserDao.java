package com.unibs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.unibs.models.User;

public class UserDao {

    public static User findByUsername(String username) throws DatabaseException {
        String sql = "SELECT username, password, role, last_login FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("role"),
                        rs.getObject("last_login", LocalDate.class));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Impossibile recuperare i dati relativi all'utente.");
        }

        return null;
    }

    public static void registerUser(String username, String newPassword, LocalDate last_login)
            throws DatabaseException {
        // If we change the password we are at first login, so we also have to edit the
        // unregistered bool
        String sql = "UPDATE user SET password = ?, last_login = ? WHERE username = ?";
        try (
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setDate(2, java.sql.Date.valueOf(last_login));
            stmt.setString(3, username);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Impossibile modificare la password.");

        }
    }

    public static int updateLastLogin(String username) throws DatabaseException {
            String sql = "UPDATE user SET last_login = ? WHERE username = ?";
            try (
                    Connection conn = DatabaseManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                stmt.setString(2, username);
                return stmt.executeUpdate();

            } catch (SQLException e) {
                throw new DatabaseException("Impossibile recuperare i dati relativi all'utente.");
            }
    }
}