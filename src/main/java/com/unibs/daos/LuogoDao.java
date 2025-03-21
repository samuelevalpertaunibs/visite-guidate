package com.unibs.daos;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.sql.*;
import java.util.ArrayList;

public class LuogoDao {
    public static ArrayList<Luogo> getAllLuoghi() throws DatabaseException {
        String sql = "SELECT * FROM luogo";
        ArrayList<Luogo> luoghi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Chiamata al database per ottenere i dati
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String descrizione = rs.getString("descrizione");
                    String comuneFk = rs.getString("comune_fk");

                    Comune comune = new Comune(comuneFk, null, null);
                    Luogo luogo = new Luogo(nome, descrizione, comune);
                    luoghi.add(luogo);
                }
            }

            // Per evitare problemi con il ResultSet associo i comuni dopo aver concluso la prima query
            for (Luogo luogo : luoghi) {
                Comune comune = ComuneDao.getComuneByNome(luogo.getNomeComune());
                luogo.setComune(comune);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero dei luoghi: " + e.getMessage(), e);
        }

        return luoghi;
    }


    public static Luogo aggiungiLuogo(Luogo luogo) throws DatabaseException {
        String sql = "INSERT INTO luogo (nome, descrizione, comune_fk) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, luogo.getNome());
            stmt.setString(2, luogo.getDescrizione());
            stmt.setString(3, luogo.getNomeComune());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Nessuna riga modificata.");
            }

            return luogo;

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static Luogo cercaLuogoPerNome(String nome) throws DatabaseException {
        String sql = "SELECT * FROM luogo WHERE nome = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String descrizione = rs.getString("descrizione");
                String comuneFk = rs.getString("comune_fk");

                Comune comune = ComuneDao.getComuneByNome(comuneFk);

                return new Luogo(nome, descrizione, comune);
            } else {
                return null;
            }


        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la ricerca del luogo per nome: " + e.getMessage(), e);
        }
    }

    public static boolean esisteLuogo(String nome) throws DatabaseException {
        String sql = "SELECT 1 FROM luogo WHERE nome = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la verifica dell'esistenza del luogo: " + e.getMessage(), e);
        }
    }
}

