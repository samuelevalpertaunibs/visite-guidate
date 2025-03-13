package com.unibs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.unibs.models.Config;
import com.unibs.models.Comune;

public class ConfigDao {

    public static Config getConfig() throws DatabaseException {
        String sql = "SELECT * FROM config";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Comune[] ambitoTerritoriale = getAmbitoTerritoriale(rs.getInt("config_id"));
                return new Config(ambitoTerritoriale, rs.getInt("numero_max_iscrizioni"));
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return null;
    }

    public static Comune[] getAmbitoTerritoriale(int config_id) throws DatabaseException {
        String sql = "SELECT * FROM comune WHERE config_id = ?";
        ArrayList<Comune> ambitoTerritoriale = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config_id);
            ResultSet rs = stmt.executeQuery();

            Comune comune;
            while (rs.next()) {
                String nome = rs.getString("nome");
                String provincia = rs.getString("provincia");
                String regione = rs.getString("regione");
                comune = new Comune(nome, provincia, regione);
                ambitoTerritoriale.add(comune);
            }

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return ambitoTerritoriale.toArray(new Comune[0]);
    }

}
