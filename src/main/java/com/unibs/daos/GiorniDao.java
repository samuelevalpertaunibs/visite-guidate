package com.unibs.daos;

import com.unibs.models.Giorno;
import com.unibs.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class GiorniDao {

    public List<Giorno> findAll() throws SQLException {
        List<Giorno> giorni = new ArrayList<>();
        String query = "select id, nome from giorni_settimana ORDER BY id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                giorni.add(new Giorno(id, nome));
            }
        }

        return giorni;
    }

}

