package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Volontario;

public class VolontarioDao {

    public static List<Volontario> getAllVolontari() {
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
        } catch (Exception e) {
            throw new DatabaseException("Errore nel recupero degi volontari: " + e.getMessage());
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
}