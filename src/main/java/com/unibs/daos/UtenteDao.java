package com.unibs.daos;

import com.unibs.models.Utente;
import com.unibs.models.Volontario;
import com.unibs.utils.DatabaseException;
import com.unibs.utils.DatabaseManager;
import com.unibs.utils.DateService;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class UtenteDao {

    public Utente findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Utente(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"), rs.getInt("ruolo_id"), rs.getObject("last_login", LocalDate.class));
            }
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        return null;
    }

    public void updatePassword(Utente utente) throws DatabaseException {
        String sql = "UPDATE utenti SET password_hash = ?, last_login = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, utente.getPasswordHash());
            stmt.setDate(2, java.sql.Date.valueOf(utente.getLastLogin()));
            stmt.setString(3, utente.getUsername());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("Errore durante l'aggiornamento della password, utente non trovato.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante l'aggiornamento della password: " + e.getMessage(), e);
        }
    }

    public LocalDate updateLastLogin(String username) throws SQLException {
        String sql = "UPDATE utenti SET last_login = ? WHERE username = ?";
        LocalDate now = DateService.today();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(now));
            stmt.setString(2, username);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Nessun utente aggiornato per username.");
            }

            return now;
        }
    }

    public List<Volontario> getAllVolontari() throws SQLException {
        ArrayList<Volontario> volontari = new ArrayList<>();
        String query = "SELECT * FROM utenti WHERE ruolo_id = 2";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                byte[] salt = rs.getBytes("salt");
                LocalDate lastLogin = rs.getObject("last_login", LocalDate.class);
                volontari.add(new Volontario(id, username, passwordHash, salt, lastLogin));
            }
        }
        return volontari;
    }

    public Set<Volontario> findVolontariByTipoVisitaId(int tipoVisitaId) throws SQLException {
        Set<Volontario> volontari = new HashSet<>();
        String query = "SELECT u.id, u.username FROM utenti u JOIN tipi_visita_volontari tvv ON u.id = tvv.volontario_id WHERE tvv.tipo_visita_id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tipoVisitaId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");

                volontari.add(new Volontario(id, username, null, null, null));
            }
        }

        return volontari;
    }

    public List<LocalDate> getDateDisponibiliByMese(int volontarioID, YearMonth mese) throws SQLException {
        String sql = "SELECT data_disponibile FROM disponibilita " + "WHERE volontario_id = ? AND YEAR(data_disponibile) = ? AND MONTH(data_disponibile) = ?";

        List<LocalDate> dateDisponibili = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, volontarioID);
            statement.setInt(2, mese.getYear());
            statement.setInt(3, mese.getMonthValue());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    dateDisponibili.add(rs.getDate("data_disponibile").toLocalDate());
                }
            }
        }

        return dateDisponibili;
    }

    public void sovrascriviDisponibilita(Volontario volontario, List<LocalDate> selezionate) throws SQLException {
        if (selezionate == null || selezionate.isEmpty()) {
            return;
        }

        LocalDate primaData = selezionate.get(0);
        int mese = primaData.getMonthValue();
        int anno = primaData.getYear();

        String deleteQuery = "DELETE FROM disponibilita WHERE volontario_id = ? AND EXTRACT(MONTH FROM data_disponibile) = ? AND EXTRACT(YEAR FROM data_disponibile) = ?";
        String insertQuery = "INSERT INTO disponibilita (volontario_id, data_disponibile) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery); PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                deleteStmt.setInt(1, volontario.getId());
                deleteStmt.setInt(2, mese);
                deleteStmt.setInt(3, anno);
                deleteStmt.executeUpdate();

                // Inserisce le nuove date
                for (LocalDate data : selezionate) {
                    insertStmt.setInt(1, volontario.getId());
                    insertStmt.setDate(2, Date.valueOf(data));
                    insertStmt.addBatch();
                }

                insertStmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void rimuovi(int id) throws SQLException {
        String sql = "DELETE FROM utenti WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Integer> getIdVolontariNonAssociati() throws SQLException {
        String sql = "SELECT id FROM utenti WHERE ruolo_id = 2 AND id NOT IN (SELECT volontario_id FROM tipi_visita_volontari)";
        List<Integer> idVolontari = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                idVolontari.add(rs.getInt("id"));
            }
        }

        return idVolontari;
    }

    public Set<Volontario> getVolontariNonAssociatiByTipoVisitaId(int tipoVisitaId) throws SQLException {
        String sql = "SELECT id, username FROM utenti WHERE ruolo_id = 2 AND id NOT IN (SELECT tipi_visita_volontari.volontario_id FROM tipi_visita_volontari WHERE tipo_visita_id = ?)";
        Set<Volontario> volontari = new HashSet<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tipoVisitaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");

                volontari.add(new Volontario(id, username, null, null, null));
            }
        }

        return volontari;
    }

    public void associaATipoVisitaById(int id, int tipoVisitaId) throws SQLException {
        String sql = "INSERT INTO tipi_visita_volontari (volontario_id, tipo_visita_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, tipoVisitaId);

            stmt.executeUpdate();
        }
    }

    public void inserisciUtente(Utente nuovoUtente) throws SQLException {
        String sql = "INSERT INTO utenti VALUES (DEFAULT, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuovoUtente.getUsername());
            stmt.setString(2, nuovoUtente.getPasswordHash());
            stmt.setBytes(3, nuovoUtente.getSalt());
            stmt.setInt(4, nuovoUtente.getRole());
            if ((nuovoUtente.getLastLogin() == null)) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, Date.valueOf(nuovoUtente.getLastLogin()));
            }

            stmt.executeUpdate();
        }
    }

    public void applicaRimozioneVolontari() throws SQLException {
        String sql = "DELETE FROM utenti WHERE id IN (SELECT volontario_id FROM rimozioni_volontari WHERE mese_rimozione = (SELECT MONTH(periodo_corrente) + 1 FROM config))";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        }
    }

    public Optional<Integer> getIdByUsername(String nome) throws SQLException {
        String sql = "SELECT id FROM utenti WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return Optional.of(rs.getInt("id"));

        }
        return Optional.empty();
    }

    public void inserisciVolontarioDaRimuovere(int id) throws SQLException {
        String sql = "INSERT INTO rimozioni_volontari (volontario_id, mese_rimozione) VALUES (?, (SELECT MONTH(periodo_corrente) + 2 FROM config))";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void terminaTVAssociatiAlVolontario(int idVolontario) throws SQLException {
        String sql = """
                UPDATE tipi_visita
                    SET data_fine = (
                        SELECT LAST_DAY(DATE_ADD(periodo_corrente, INTERVAL 1 MONTH))
                        FROM config
                    )
                    WHERE id IN (
                        SELECT tvv.tipo_visita_id
                        FROM tipi_visita_volontari tvv
                        GROUP BY tvv.tipo_visita_id
                        HAVING COUNT(*) = 1 AND MAX(tvv.volontario_id) = ?
                    );
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVolontario);
            stmt.executeUpdate();
        }
    }
}
