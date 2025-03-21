package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;

public class VolontariDao {

    public static ArrayList<String> getListaVolontari() {
        ArrayList<String> volontari = new ArrayList<>();
        String query = "SELECT username FROM user WHERE role = 'VOL'";

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
}
