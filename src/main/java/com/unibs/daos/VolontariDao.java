package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;

public class VolontariDao {

    public static ArrayList<String> getListaVolontari() {
        ArrayList<String> volontari = new ArrayList<>();
        String query = "SELECT username FROM utenti WHERE ruolo_id = 2";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                volontari.add(rs.getString("username"));
            }

        } catch (Exception e) {
            throw new DatabaseException("Errore nel recupero dei volontari: " + e.getMessage());
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