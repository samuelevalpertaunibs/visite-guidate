package com.unibs.daos;

import com.unibs.mappers.RowMapper;
import com.unibs.models.Luogo;
import com.unibs.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LuogoDao {

    private final RowMapper<Luogo> luogoMapper;

    public LuogoDao(RowMapper<Luogo> luogoMapper) {
        this.luogoMapper = luogoMapper;
    }

    public List<Luogo> findAll() throws SQLException {
        String sql = """
                    SELECT
                        l.id AS luogo_id,
                        l.nome AS luogo_nome,
                        l.descrizione AS luogo_descrizione,
                        c.id AS comune_id,
                        c.nome AS comune_nome,
                        c.provincia AS comune_provincia,
                        c.regione AS comune_regione
                    FROM luoghi l
                    JOIN comuni c ON l.comune_id = c.id
                """;
        List<Luogo> luoghi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                luoghi.add(luogoMapper.map(rs));
            }
        }

        return luoghi;
    }

    public Optional<Luogo> findByNome(String nome) throws SQLException {
        String sql = """
            SELECT
                l.id AS luogo_id,
                l.nome AS luogo_nome,
                l.descrizione AS luogo_descrizione,
                c.id AS comune_id,
                c.nome AS comune_nome,
                c.provincia AS comune_provincia,
                c.regione AS comune_regione
            FROM luoghi l
            JOIN comuni c ON l.comune_id = c.id
            WHERE l.nome = ?
        """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(luogoMapper.map(rs));
                }
            }
        }

        return Optional.empty();
    }

    public Luogo aggiungiLuogo(Luogo luogo) throws SQLException {
        String sql = "INSERT INTO luoghi (nome, descrizione, comune_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, luogo.getNome());
            stmt.setString(2, luogo.getDescrizione());
            stmt.setInt(3, luogo.getComuneId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creazione luogo fallita: nessuna riga inserita.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    luogo.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creazione luogo fallita: nessun ID generato.");
                }
            }

            return luogo;
        }
    }

    public void rimuovi(int id) throws SQLException {
        String sql = "DELETE FROM luoghi WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Integer> getIdNonAssociati() throws SQLException {
        String sql = "SELECT id FROM luoghi WHERE id NOT IN (SELECT luogo_id FROM tipi_visita)";
        List<Integer> idLuoghi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                idLuoghi.add(rs.getInt("id"));
            }
        }

        return idLuoghi;
    }

    public void inserisciLuogoDaRimuovere(int id) throws SQLException {
        String sql = "INSERT INTO rimozioni_luoghi (luogo_id, mese_rimozione) VALUES (?, (SELECT MONTH(periodo_corrente) + 2 FROM config))";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void applicaRimozioneLuoghi() throws SQLException {
        String sql = "DELETE FROM luoghi WHERE id IN (SELECT luogo_id FROM rimozioni_luoghi WHERE mese_rimozione = (SELECT MONTH(periodo_corrente) + 1 FROM config))";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        }
    }

    public void terminaTVAssociatiAlLuogo(Integer idLuogo) throws SQLException {
        String sql = """
                UPDATE tipi_visita
                SET data_fine = (
                    SELECT LAST_DAY(DATE_ADD(periodo_corrente, INTERVAL 1 MONTH))
                    FROM config
                )
                WHERE luogo_id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idLuogo);
            stmt.executeUpdate();
        }
    }
}