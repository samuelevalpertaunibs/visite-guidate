package com.unibs.daos;

import com.unibs.models.Config;
import com.unibs.utils.DatabaseManager;
import com.unibs.mappers.RowMapper;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class ConfigDao {

    private static final int DEFAULT_CONFIG_ID = 1;
    private final RowMapper<Config> configMapper;

    public ConfigDao(RowMapper<Config> configMapper) {
        this.configMapper = configMapper;
    }

    public Optional<Config> getConfig() throws SQLException {
        String sql = "SELECT * FROM config WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, DEFAULT_CONFIG_ID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(configMapper.map(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void initDefault() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String deleteExistingSql = "DELETE FROM config WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteExistingSql)) {
                deleteStmt.setInt(1, DEFAULT_CONFIG_ID);
                deleteStmt.executeUpdate();
            }

            String insertConfigSql =
                    "INSERT INTO config (id, numero_max_iscrizioni, periodo_corrente) VALUES (?, NULL, NULL)";
            try (PreparedStatement stmt = conn.prepareStatement(insertConfigSql)) {
                stmt.setInt(1, DEFAULT_CONFIG_ID);
                stmt.executeUpdate();
            }
        }
    }

    public void setNumeroMax(int numeroMassimoIscrizioniPrenotazione) throws SQLException {
        String sql = "UPDATE config SET numero_max_iscrizioni = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(sql)) {

            updateStmt.setInt(1, numeroMassimoIscrizioniPrenotazione);
            updateStmt.setInt(2, DEFAULT_CONFIG_ID);
            updateStmt.executeUpdate();
        }
    }

    public void setPeriodoCorrente(LocalDate periodoCorrente) throws SQLException {
        String sql = "UPDATE config SET periodo_corrente = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(sql)) {

            updateStmt.setDate(1, Date.valueOf(periodoCorrente));
            updateStmt.setInt(2, DEFAULT_CONFIG_ID);
            updateStmt.executeUpdate();
        }
    }
}