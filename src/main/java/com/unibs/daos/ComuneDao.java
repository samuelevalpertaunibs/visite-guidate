package com.unibs.daos;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Comune;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ComuneDao {
    public static Comune getComuneById(int id) throws DatabaseException {
        String sql = "SELECT * FROM comuni WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String provincia = rs.getString("provincia");
                    String regione = rs.getString("regione");
                    return new Comune(id, nome, provincia, regione);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero del comune: " + e.getMessage(), e);
        }
        return null;
    }

    public static ArrayList<Comune> getAllComuni() throws DatabaseException {
        String sql = "SELECT * FROM comuni";
        ArrayList<Comune> comuni = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String provincia = rs.getString("provincia");
                String regione = rs.getString("regione");

                Comune comune = new Comune(id, nome, provincia, regione);
                comuni.add(comune);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero dei luoghi: " + e.getMessage(), e);
        }

        return comuni;
    }
}
