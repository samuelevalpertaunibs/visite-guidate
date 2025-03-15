package com.unibs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.unibs.models.Config;
import com.unibs.models.Comune;

public class ConfigDao {

    public static Config getConfig() throws DatabaseException {
        String sql = "SELECT * FROM config LIMIT 1"; // Limita a una sola riga
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int configId = rs.getInt("id");
                int numeroMaxIscrizioni = rs.getInt("numero_max_iscrizioni");
                ArrayList<Comune> ambitoTerritoriale = getAmbitoTerritoriale(configId);

                return new Config(ambitoTerritoriale, numeroMaxIscrizioni);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero della configurazione: " + e.getMessage(), e);
        }
    }


    public static ArrayList<Comune> getAmbitoTerritoriale(int config_id) throws DatabaseException {
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

        return ambitoTerritoriale;
    }

    public static Config aggiungiComune(Comune comune) {
        String insertSql = "INSERT INTO comune (nome, provincia, regione, config_id) VALUES (?, ?, ?, 1)";
        String selectSql = "SELECT nome, provincia, regione FROM comune WHERE config_id = 1";

        ArrayList<Comune> ambitoTerritoriale = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            try {
                conn.setAutoCommit(false); // Inizio transazione

                // Inserisco il comune in batch
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, comune.getNome());
                    insertStmt.setString(2, comune.getProvincia());
                    insertStmt.setString(3, comune.getRegione());
                    insertStmt.addBatch();
                    insertStmt.executeBatch();
                }

                // Recupero i comuni aggiornati
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                     ResultSet rs = selectStmt.executeQuery()) {

                    while (rs.next()) {
                        String nome = rs.getString("nome");
                        String provincia = rs.getString("provincia");
                        String regione = rs.getString("regione");
                        ambitoTerritoriale.add(new Comune(nome, provincia, regione));
                    }
                }

                conn.commit(); // Conferma transazione

            } catch (SQLException e) {
                conn.rollback(); // Annulla tutto in caso di errore
                throw new DatabaseException("Errore nell'aggiornamento delle configurazioni: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true); // Ripristina modalità predefinita
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore di connessione al database: " + e.getMessage(), e);
        }

        return new Config(ambitoTerritoriale, 1);
    }


    public static boolean doesInclude(String nome, String provincia, String regione) {
        String sql = "SELECT COUNT(*) FROM comune WHERE LOWER(nome) = LOWER(?) AND LOWER(provincia) = LOWER(?) AND LOWER(regione) = LOWER(?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome.toLowerCase());
            stmt.setString(2, provincia.toLowerCase());
            stmt.setString(3, regione.toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Errore nel controllo dell'esistenza del comune.");
        }

        return false;
    }

    public static Config initDefault() throws DatabaseException {
        // Salva i dati di default nel database
        try (Connection conn = DatabaseManager.getConnection()) {
            String deleteExistingSql = "DELETE FROM config";
            // Elimina la configurazione gia esistente
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteExistingSql)) {
                deleteStmt.executeUpdate();
            }
            // Inserisci i dati di default nel database
            String insertConfigSql = "INSERT INTO `config` (`id`, `numero_max_iscrizioni`) VALUES (DEFAULT, NULL);";
            try (PreparedStatement stmt = conn.prepareStatement(insertConfigSql)) {
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inizializzazione della configurazione: " + e.getMessage());
        }

        // Restituisci la Config creata
        return getConfig();
    }

}
