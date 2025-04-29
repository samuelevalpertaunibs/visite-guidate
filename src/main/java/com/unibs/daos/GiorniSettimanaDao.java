package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.unibs.DatabaseManager;
import com.unibs.models.Giorno;


public class GiorniSettimanaDao {

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

    public Set<Giorno> findByTipoVisitaId(int tipoVisitaId) {
        Set<Giorno> giorni = new HashSet<>();
        String query = "SELECT g.id, g.nome FROM giorni_settimana g JOIN giorni_settimana_tipi_visita tvg ON g.id = tvg.giorno_settimana_id WHERE tvg.tipo_visita_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, tipoVisitaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                giorni.add(new Giorno(id, nome));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // oppure loggalo in modo più formale
        }

        return giorni;
    }
}

