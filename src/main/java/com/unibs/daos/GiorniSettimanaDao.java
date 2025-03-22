package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;


public class GiorniSettimanaDao {

    public static ArrayList<String> getGiorniSettimana() {
        ArrayList<String> giorni = new ArrayList<>();
        String query = "select nome from giorni_settimana ORDER BY id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                giorni.add(rs.getString("nome"));
            }

        } catch (Exception e) {
            throw new DatabaseException("Errore nel recupero dei giorni della settimana: " + e.getMessage());
        }

        return giorni;
    }

    public static int getIdByNome(String nome) {
        String sql = "SELECT id FROM giorni_settimana WHERE nome = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

            return -1;

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la ricerca del giorno per nome: " + e.getMessage());
        }

    }
}

