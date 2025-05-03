package com.unibs.daos;

import com.unibs.models.Comune;
import com.unibs.models.Config;
import com.unibs.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigDao {

    private static final int DEFAULT_CONFIG_ID = 1;

    public Optional<Config> getConfig() throws SQLException {
        String sql = "SELECT * FROM config LIMIT 1"; // Limita a una sola riga
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int numeroMaxIscrizioni = rs.getInt("numero_max_iscrizioni");
                Date sqlDate = rs.getDate("periodo_corrente");
                LocalDate initializedOn = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                return Optional.of(new Config(null, numeroMaxIscrizioni, initializedOn));
            }

            return Optional.empty();
        }
    }

    public List<Comune> getAmbitoTerritoriale() throws SQLException {
        String sql = "SELECT * FROM comuni WHERE config_id = ?";
        ArrayList<Comune> ambitoTerritoriale = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, DEFAULT_CONFIG_ID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String provincia = rs.getString("provincia");
                String regione = rs.getString("regione");
                Comune comune = new Comune(id, nome, provincia, regione);
                ambitoTerritoriale.add(comune);
            }
            return ambitoTerritoriale;
        }
    }

    public void aggiungiComune(Comune comune) throws SQLException {
        String insertSql = "INSERT INTO comuni (nome, provincia, regione, config_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, comune.getNome());
            insertStmt.setString(2, comune.getProvincia());
            insertStmt.setString(3, comune.getRegione());
            insertStmt.setInt(4, DEFAULT_CONFIG_ID);
            insertStmt.executeUpdate();
        }
    }

    public void initDefault() throws SQLException {
        // Salva i dati di default nel database
        try (Connection conn = DatabaseManager.getConnection()) {
            String deleteExistingSql = "DELETE FROM config where id = ?";
            // Elimina la configurazione gia esistente
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteExistingSql)) {
                deleteStmt.setInt(1, DEFAULT_CONFIG_ID);
                deleteStmt.executeUpdate();
            }
            // Inserisci i dati di default nel database
            String insertConfigSql = "INSERT INTO config (id, numero_max_iscrizioni, periodo_corrente) VALUES (?, NULL, NULL)";
            try (PreparedStatement stmt = conn.prepareStatement(insertConfigSql)) {
                stmt.setInt(1, DEFAULT_CONFIG_ID);
                stmt.executeUpdate();
            }
        }
    }

    public void setNumeroMax(int numeroMassimoIscrizioniPrenotazione) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE config SET numero_max_iscrizioni = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                updateStmt.setInt(1, numeroMassimoIscrizioniPrenotazione);
                updateStmt.setInt(2, DEFAULT_CONFIG_ID);
                updateStmt.executeUpdate();
            }
        }
    }

    public void setPeriodoCorrente(LocalDate periodoCorrente) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE config SET periodo_corrente = ?  WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(sql)) {
                updateStmt.setDate(1, Date.valueOf(periodoCorrente));
                updateStmt.setInt(2, DEFAULT_CONFIG_ID);
                updateStmt.executeUpdate();
            }
        }
    }
}
