package com.unibs.daos;

import com.unibs.models.Comune;
import com.unibs.models.Luogo;
import com.unibs.utils.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LuogoDao {
    public List<Luogo> findAll() throws SQLException {
        String sql = "SELECT * FROM luoghi";
        ArrayList<Luogo> luoghi = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                luoghi.add(mapLuogo(rs));
            }
        }

        return luoghi;
    }

    private Luogo mapLuogo(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String descrizione = rs.getString("descrizione");
        int comuneId = rs.getInt("comune_id");

        // Comune parziale, completato nel service
        Comune comune = new Comune(comuneId, null, null, null);
        return new Luogo(id, nome, descrizione, comune);
    }

    public Luogo aggiungiLuogo(Luogo luogo) throws SQLException {
        String sql = "INSERT INTO luoghi (nome, descrizione, comune_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, luogo.getNome());
            stmt.setString(2, luogo.getDescrizione());
            stmt.setInt(3, luogo.getComuneId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Nessuna riga modificata durante l'inserimento del luogo.");
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

    public Optional<Luogo> findByNome(String nome) throws SQLException {
        String sql = "SELECT * FROM luoghi WHERE nome = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Luogo luogoTrovato = mapLuogo(rs);
                    return Optional.of(luogoTrovato);
                }
            }
        }

        return Optional.empty();
    }

}

