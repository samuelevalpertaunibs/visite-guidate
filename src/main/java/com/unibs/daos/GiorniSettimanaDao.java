package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;


public class GiorniSettimanaDao {

    public static ArrayList<String> getGiorniSettimana() {
        ArrayList<String> giorni = new ArrayList<>();
        String query = "select nome from giorni_settimana ORDER BY FIELD(nome, 'Lunedì', 'Martedì', 'Mercoledì', 'Giovedì', 'Venerdì', 'Sabato', 'Domenica')";

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
}

