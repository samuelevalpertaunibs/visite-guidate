package com.unibs.daos;

import com.unibs.utils.DatabaseManager;
import com.unibs.models.Comune;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class ComuneDao {
    public Optional<Comune> findById(int id) throws SQLException {
        String sql = "SELECT * FROM comuni WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String provincia = rs.getString("provincia");
                    String regione = rs.getString("regione");
                    return Optional.of(new Comune(id, nome, provincia, regione));
                }
            }
        }
        return Optional.empty();
    }

    // Se un record nel database non esiste o non è valido, semplicemente non lo aggiungo alla lista, quindi non uso Optional
    public ArrayList<Comune> findAll() throws SQLException {
        String sql = "SELECT * FROM comuni";
        ArrayList<Comune> comuni = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comuni.add(mapComune(rs));
            }
        }

        return comuni;
    }

    private Comune mapComune(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String provincia = rs.getString("provincia");
        String regione = rs.getString("regione");
        return new Comune(id, nome, provincia, regione);
    }
}
