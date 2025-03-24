package com.unibs.daos;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.sql.*;
import java.util.ArrayList;

public class LuogoDao {
    public static ArrayList<Luogo> getAllLuoghi() throws DatabaseException {
        String sql = "SELECT * FROM luoghi";
        ArrayList<Luogo> luoghi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Chiamata al database per ottenere i dati
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int  id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String descrizione = rs.getString("descrizione");
                    int comuneId = rs.getInt("comune_id");

                    Comune comune = new Comune(comuneId, null, null, null);
                    Luogo luogo = new Luogo(id, nome, descrizione, comune);
                    luoghi.add(luogo);
                }
            }

            // Per evitare problemi con il ResultSet associo i comuni dopo aver concluso la prima query
            for (Luogo luogo : luoghi) {
                Comune comune = ComuneDao.getComuneById(luogo.getIdComune());
                luogo.setComune(comune);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero dei luoghi: " + e.getMessage(), e);
        }

        return luoghi;
    }


    public static Luogo aggiungiLuogo(Luogo luogo) throws DatabaseException {
        String sql = "INSERT INTO luoghi (nome, descrizione, comune_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, luogo.getNome());
            stmt.setString(2, luogo.getDescrizione());
            stmt.setInt(3, luogo.getIdComune());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseException("Nessuna riga modificata.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    luogo.setId(generatedKeys.getInt(1));
                } else {
                    throw new DatabaseException("Creazione luogo fallita, nessun ID generato.");
                }
            }

            return luogo;

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    public static int getIdByNome(String nome) throws DatabaseException {
        String sql = "SELECT id FROM luoghi WHERE nome = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

            return -1;


        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la ricerca del luogo per nome: " + e.getMessage());
        }
    }

    public static boolean esisteLuogo(String nome) throws DatabaseException {
        String sql = "SELECT 1 FROM luoghi WHERE nome = ? LIMIT 1";

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

    public static Luogo getLuogoByNome(String nomeLuogoSelezionato) throws DatabaseException {
        String sql = "SELECT * FROM luoghi WHERE nome = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeLuogoSelezionato);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String descrizione = rs.getString("descrizione");
                    int comuneId = rs.getInt("comune_id");

                    Comune comune = ComuneDao.getComuneById(comuneId); // Recupero il comune associato
                    return new Luogo(id, nome, descrizione, comune);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la ricerca del luogo: " + e.getMessage(), e);
        }
        return null;
    }

}

