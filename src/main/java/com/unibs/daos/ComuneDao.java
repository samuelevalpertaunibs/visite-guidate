package com.unibs.daos;

import com.unibs.mappers.RowMapper;
import com.unibs.models.Comune;
import com.unibs.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComuneDao {
    private final RowMapper<Comune> comuneMapper;

    public ComuneDao(RowMapper<Comune> comuneMapper) {
        this.comuneMapper = comuneMapper;
    }

    public Optional<Comune> findById(int id) throws SQLException {
        String sql = """
                    SELECT
                        c.id AS comune_id,
                        c.nome AS comune_nome,
                        c.provincia AS comune_provincia,
                        c.regione AS comune_regione
                    FROM comuni c
                    WHERE c.id = ?
                """;
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(comuneMapper.map(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Comune> findAll() throws SQLException {
        String sql = """
                    SELECT
                        c.id AS comune_id,
                        c.nome AS comune_nome,
                        c.provincia AS comune_provincia,
                        c.regione AS comune_regione
                    FROM comuni c
                """;
        List<Comune> comuni = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comuni.add(comuneMapper.map(rs));
            }
        }

        return comuni;
    }

    public void aggiungiComune(Comune comune) throws SQLException {
        String insertSql = "INSERT INTO comuni (nome, provincia, regione) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            insertStmt.setString(1, comune.nome());
            insertStmt.setString(2, comune.provincia());
            insertStmt.setString(3, comune.regione());
            insertStmt.executeUpdate();
        }
    }
}
