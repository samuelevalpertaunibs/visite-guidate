package com.unibs.daos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Config;
import com.unibs.models.Comune;

public class ConfigDao {

    public static Config getConfig() throws DatabaseException {
        String sql = "SELECT * FROM config LIMIT 1"; // Limita a una sola riga
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int numeroMaxIscrizioni = rs.getInt("numero_max_iscrizioni");
                Date sqlDate = rs.getDate("initialized_on");
                LocalDate initializedOn = (sqlDate != null) ? sqlDate.toLocalDate() : null;
                ArrayList<Comune> ambitoTerritoriale = getAmbitoTerritoriale();

                return new Config(ambitoTerritoriale, numeroMaxIscrizioni, initializedOn);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero della configurazione: " + e.getMessage(), e);
        }
    }

    public static ArrayList<Comune> getAmbitoTerritoriale() throws DatabaseException {
        String sql = "SELECT * FROM comuni WHERE config_id = 1";
        ArrayList<Comune> ambitoTerritoriale = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            Comune comune;
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String provincia = rs.getString("provincia");
                String regione = rs.getString("regione");
                comune = new Comune(id, nome, provincia, regione);
                ambitoTerritoriale.add(comune);
            }

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return ambitoTerritoriale;
    }

    public static void aggiungiComune(Comune comune) {
        String insertSql = "INSERT INTO comuni (nome, provincia, regione, config_id) VALUES (?, ?, ?, 1)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, comune.getNome());
            insertStmt.setString(2, comune.getProvincia());
            insertStmt.setString(3, comune.getRegione());
            insertStmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Errore nell'aggiornamento delle configurazioni: " + e.getMessage(), e);
        }
    }

    public static boolean doesInclude(String nome, String provincia, String regione) {
        String sql = "SELECT COUNT(*) FROM comuni WHERE LOWER(nome) = LOWER(?) AND LOWER(provincia) = LOWER(?) AND LOWER(regione) = LOWER(?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome.toLowerCase());
            stmt.setString(2, provincia.toLowerCase());
            stmt.setString(3, regione.toLowerCase());

            ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Errore nel controllo dell'esistenza del comune.");
        }
        return false;
    }

    public static void initDefault() throws DatabaseException {
        // Salva i dati di default nel database
        try (Connection conn = DatabaseManager.getConnection()) {
            String deleteExistingSql = "DELETE FROM config where id = 1";
            // Elimina la configurazione gia esistente
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteExistingSql)) {
                deleteStmt.executeUpdate();
            }
            // Inserisci i dati di default nel database
            String insertConfigSql = "INSERT INTO `config` (`id`, `numero_max_iscrizioni`, `initialized_on`) VALUES (DEFAULT, NULL, NULL)";
            try (PreparedStatement stmt = conn.prepareStatement(insertConfigSql)) {
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inizializzazione della configurazione: " + e.getMessage());
        }

    }

    public static void setNumeroMax(int numeroMassimoIscrizioniPrenotazione) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE config SET numero_max_iscrizioni = ? WHERE id = 1";
            try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                updateStmt.setInt(1, numeroMassimoIscrizioniPrenotazione);
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inizializzazione della configurazione: " + e.getMessage());
        }

    }

    public static void setInitializedOn(LocalDate initializedOn) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE config SET initialized_on = ?  WHERE id = 1";
            try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                updateStmt.setDate(1, Date.valueOf(initializedOn));
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'inizializzazione della configurazione: " + e.getMessage());
        }

    }

    public static int getNumeroComuni() {
        String sql = "SELECT COUNT(*) FROM comuni WHERE config_id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            return (rs.next()) ? rs.getInt(1) : 0;

        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static int getNumeroMax() {
        String sql = "SELECT numero_max_iscrizioni FROM config WHERE id = 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            if  (rs.next())
                return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
