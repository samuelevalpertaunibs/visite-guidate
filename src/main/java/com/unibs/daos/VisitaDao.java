package com.unibs.daos;

import com.unibs.mappers.VisitaMapper;
import com.unibs.models.TipoVisitaCore;
import com.unibs.models.Visita;
import com.unibs.utils.DatabaseManager;
import com.unibs.utils.DateService;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class VisitaDao {

    private final VisitaMapper visitaMapper;

    public VisitaDao(VisitaMapper visitaMapper) {
        this.visitaMapper = visitaMapper;
    }

    public List<Visita> getVisitePreview(Visita.StatoVisita stato) throws SQLException {
        String sql = """
                                SELECT v.id AS visita_id,
                                       v.tipo_visita_id AS visita_tipo_visita_id,
                                       t.indirizzo_incontro AS tv_indirizzo_incontro,
                                       t.comune_incontro AS tv_comune_incontro,
                                       t.provincia_incontro AS tv_provincia_incontro,
                                       t.titolo AS tv_titolo,
                                       t.descrizione AS tv_descrizione,
                                       t.data_inizio AS tv_data_inizio,
                                       t.data_fine AS tv_data_fine,
                                       t.ora_inizio AS tv_ora_inizio,
                                       t.durata_minuti AS tv_durata_minuti,
                                       t.entrata_libera AS tv_entrata_libera,
                                       t.num_min_partecipanti AS tv_num_min_partecipanti,
                                       t.num_max_partecipanti AS tv_num_max_partecipanti,
                                       v.data_svolgimento AS visita_data_svolgimento,
                                       v.stato AS visita_stato,
                                       u.username AS utente_username,
                                       u.id AS utente_id,
                                       l.nome AS luogo_nome
                                FROM visite v
                                JOIN tipi_visita t ON v.tipo_visita_id = t.id
                                JOIN utenti u ON v.volontario_id = u.id
                                JOIN luoghi l ON t.luogo_id = l.id
                                WHERE v.stato = ?
                                ORDER BY v.data_svolgimento
                """;

        Map<Integer, TipoVisitaCore> cache = new HashMap<>();
        List<Visita> visite = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Visita visita = visitaMapper.map(rs, cache);
                    visite.add(visita);
                }
            }
        }

        return visite;
    }

    public void inserisciVisite(List<Visita> tutteLeVisite) throws SQLException {
        String sql = "INSERT INTO visite (tipo_visita_id, volontario_id, data_svolgimento, stato) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Visita visita : tutteLeVisite) {
                stmt.setInt(1, visita.getTipoVisita().getId());
                stmt.setInt(2, (Integer) visita.getVolontario().getKey());
                stmt.setDate(3, Date.valueOf(visita.getDataSvolgimento()));
                stmt.setString(4, visita.getStato().name());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    @SuppressWarnings("SqlWithoutWhere")
    public void rimuoviDisponibilita() throws SQLException {
        java.lang.String sql = "DELETE FROM disponibilita";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate(); // Esegue direttamente la DELETE
        }
    }

    public Integer iscrivi(Integer utenteId, Integer visitaSelezionataId, Integer numeroIscritti) throws SQLException {
        java.lang.String sql = "INSERT INTO iscrizioni (fruitore_id, visita_id, numero_iscritti) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, utenteId);
            stmt.setInt(2, visitaSelezionataId);
            stmt.setInt(3, numeroIscritti);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Impossibile recuperare il codice iscrizione.");
                    }
                }
            } else {
                throw new SQLException("Inserimento non riuscito.");
            }
        }
    }

    public int getIscrizioniRimanentiById(int visitaId) throws SQLException {
        String sql = """
                SELECT tv.num_max_partecipanti - COALESCE(SUM(i.numero_iscritti),0) AS posti_disponibili
                FROM visite v
                INNER JOIN tipi_visita tv ON v.tipo_visita_id = tv.id
                LEFT JOIN iscrizioni i ON v.id = i.visita_id
                WHERE v.id = ? GROUP BY tv.num_max_partecipanti
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("posti_disponibili");
            }
        }
        return 0;
    }

    public void setStatoById(Integer visitaId, Visita.StatoVisita stato) throws SQLException {
        String sql = "UPDATE visite SET stato = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stato.name());
            stmt.setInt(2, visitaId);
            stmt.executeUpdate();
        }
    }

    public List<Visita> getVisitePreviewByFruitore(Visita.StatoVisita stato, String nomeFruitoreIscritto) throws SQLException {
        String sql = """
                SELECT DISTINCT
                       v.id AS visita_id,
                       v.tipo_visita_id AS visita_tipo_visita_id,
                       t.indirizzo_incontro AS tv_indirizzo_incontro,
                       t.comune_incontro AS tv_comune_incontro,
                       t.provincia_incontro AS tv_provincia_incontro,
                       t.titolo AS tv_titolo,
                       t.descrizione AS tv_descrizione,
                       t.data_inizio AS tv_data_inizio,
                       t.data_fine AS tv_data_fine,
                       t.ora_inizio AS tv_ora_inizio,
                       t.durata_minuti AS tv_durata_minuti,
                       t.entrata_libera AS tv_entrata_libera,
                       t.num_min_partecipanti AS tv_num_min_partecipanti,
                       t.num_max_partecipanti AS tv_num_max_partecipanti,
                       v.data_svolgimento AS visita_data_svolgimento,
                       v.stato AS visita_stato,
                       u.username AS utente_username,
                       u.id AS utente_id,
                       l.nome AS luogo_nome
                FROM visite v
                JOIN tipi_visita t ON v.tipo_visita_id = t.id
                JOIN utenti u ON v.volontario_id = u.id
                JOIN luoghi l ON t.luogo_id = l.id
                JOIN iscrizioni i ON v.id = i.visita_id
                JOIN utenti f ON i.fruitore_id = f.id
                WHERE v.stato = ? AND f.username = ?
                ORDER BY v.data_svolgimento
                """;

        Map<Integer, TipoVisitaCore> cache = new HashMap<>();
        List<Visita> visite = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            stmt.setString(2, nomeFruitoreIscritto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Visita visita = visitaMapper.map(rs, cache);
                    visite.add(visita);
                }
            }
        }

        return visite;
    }

    public List<java.lang.String> getCodiciPrenotazioneFruitorePerVista(java.lang.String nomeFruitore, Integer visitaId) throws SQLException {
        java.lang.String sql = "SELECT i.id FROM iscrizioni i " + "JOIN utenti u ON fruitore_id = u.id " + "WHERE u.username = ? AND visita_id = ?";

        List<java.lang.String> codiciPrenotazione = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeFruitore);
            stmt.setInt(2, visitaId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                codiciPrenotazione.add("" + rs.getInt("id"));
            }
        }

        return codiciPrenotazione;
    }

    public Integer getIdFruitoreByIdIscrizione(Integer codiceIscrizione) throws SQLException {
        java.lang.String sql = "SELECT fruitore_id FROM iscrizioni WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codiceIscrizione);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("fruitore_id");
            }
        }
        return null;
    }

    public Integer getIdVisitaByIdIscrizione(Integer codiceIscrizione) throws SQLException {
        java.lang.String sql = "SELECT visita_id FROM iscrizioni WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codiceIscrizione);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("visita_id");
            }
        }
        return null;
    }

    public void disdiciIscrizione(Integer codiceIscrizione) throws SQLException {
        java.lang.String sql = "DELETE FROM iscrizioni WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codiceIscrizione);
            stmt.executeUpdate();
        }
    }

    public Optional<Visita.StatoVisita> getStatoById(Integer visitaId) throws SQLException {
        String sql = "SELECT stato FROM visite WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(Visita.StatoVisita.valueOf(rs.getString("stato")));
            }
        }
        return Optional.empty();
    }


    public List<Visita> getVisitePreviewByVolontario(Visita.StatoVisita stato, java.lang.String nomeString) throws SQLException {
        List<Visita> visite = new ArrayList<>();
        Map<Integer, TipoVisitaCore> cache = new HashMap<>();

        java.lang.String sql = """
                                SELECT DISTINCT
                       v.id AS visita_id,
                       v.tipo_visita_id AS visita_tipo_visita_id,
                       t.indirizzo_incontro AS tv_indirizzo_incontro,
                       t.comune_incontro AS tv_comune_incontro,
                       t.provincia_incontro AS tv_provincia_incontro,
                       t.titolo AS tv_titolo,
                       t.descrizione AS tv_descrizione,
                       t.data_inizio AS tv_data_inizio,
                       t.data_fine AS tv_data_fine,
                       t.ora_inizio AS tv_ora_inizio,
                       t.durata_minuti AS tv_durata_minuti,
                       t.entrata_libera AS tv_entrata_libera,
                       t.num_min_partecipanti AS tv_num_min_partecipanti,
                       t.num_max_partecipanti AS tv_num_max_partecipanti,
                       v.data_svolgimento AS visita_data_svolgimento,
                       v.stato AS visita_stato,
                       l.nome AS luogo_nome,
                       u.id AS utente_id,
                       u.username AS utente_username,
                       u.id AS utente_id
                FROM visite v
                JOIN tipi_visita t ON v.tipo_visita_id = t.id
                JOIN utenti u ON v.volontario_id = u.id
                JOIN luoghi l ON t.luogo_id = l.id
                WHERE v.stato = ? AND u.username = ?
                ORDER BY v.data_svolgimento;
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            stmt.setString(2, nomeString);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    Visita visita = visitaMapper.map(rs, cache);
                    visite.add(visita);
                }
            }
        }

        return visite;
    }

    public List<java.lang.String> getCodiciPrenotazionePerVista(java.lang.String username, Integer id) throws SQLException {
        java.lang.String sql = """
                SELECT i.id as iscrizione_id, i.numero_iscritti as numero_iscritti FROM iscrizioni i
                JOIN visite v ON i.visita_id = v.id
                WHERE v.volontario_id = ?
                AND v.id = ?
                """;

        List<java.lang.String> codiciPrenotazione = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                codiciPrenotazione.add(rs.getInt("iscrizione_id") + "(" + rs.getInt("numero_iscritti") + ")");
            }
        }

        return codiciPrenotazione;
    }

    public List<Integer> getIdVisiteCompleteDaChiudere() throws SQLException {
        List<Integer> idVisite = new ArrayList<>();
        java.lang.String sql = "SELECT id FROM visite WHERE stato = 'COMPLETA' AND data_svolgimento <= ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(DateService.today().plusDays(3)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                idVisite.add(rs.getInt("id"));
            }
        }

        return idVisite;
    }

    public List<Integer> getIdVisiteDaFare() throws SQLException {
        String sql = """
                SELECT v.id
                FROM visite v
                JOIN tipi_visita tv ON tv.id = v.tipo_visita_id
                WHERE v.stato = 'PROPOSTA'
                  AND v.data_svolgimento <= ?
                  AND (SELECT COALESCE(SUM(numero_iscritti),0) FROM iscrizioni WHERE visita_id = v.id) >= tv.num_min_partecipanti
                """;

        List<Integer> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(DateService.today().plusDays(3)));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getInt("id"));
                }
            }
        }
        return result;
    }

    public List<Integer> getIdVisiteDaCancellare() throws SQLException {
        String sql = """
                SELECT v.id
                FROM visite v
                JOIN tipi_visita tv ON tv.id = v.tipo_visita_id
                WHERE v.stato = 'PROPOSTA'
                  AND v.data_svolgimento <= ?
                  AND (SELECT COALESCE(SUM(numero_iscritti),0) FROM iscrizioni WHERE visita_id = v.id) < tv.num_min_partecipanti
                """;

        List<Integer> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(DateService.today().plusDays(3)));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) result.add(rs.getInt("id"));
            }
        }
        return result;
    }

    public List<Integer> getIdVisiteDaRendereEffettuate() throws SQLException {
        String sql = "SELECT v.id FROM visite v WHERE v.stato = 'CONFERMATA' AND v.data_svolgimento < ?";
        List<Integer> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(DateService.today()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) result.add(rs.getInt("id"));
            }
        }
        return result;
    }

    public void rimuoviVisiteCancellate() throws SQLException {
        String sql = "DELETE FROM visite WHERE stato = 'CANCELLATA' AND data_svolgimento < ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(DateService.today()));
            stmt.executeUpdate();
        }
    }

    public List<Visita> getVisiteFromArchivio() throws SQLException {
        java.lang.String sql = """
                SELECT a.titolo              AS tv_titolo,
                       a.descrizione         AS tv_descrizione,
                       a.indirizzo_incontro  AS tv_indirizzo_incontro,
                       a.comune_incontro     AS tv_comune_incontro,
                       a.provincia_incontro  AS tv_provincia_incontro,
                       a.ora_inizio          AS tv_ora_inizio,
                       a.durata_minuti       AS tv_durata_minuti,
                       a.entrata_libera      AS tv_entrata_libera,
                       a.luogo_nome          AS tv_luogo_nome,
                       a.data_svolgimento    AS visita_data_svolgimento,
                       a.stato               AS visita_stato,
                       a.username_volontario AS utente_username,
                       0                     AS utente_id,
                       a.tipo_visita_id      AS tv_tipo_visita_id,
                       0                     AS tv_num_min_partecipanti,
                       0                     AS tv_num_max_partecipanti
                FROM archivio a;
                """;

        List<Visita> visite = new ArrayList<>();
        Map<Integer, TipoVisitaCore> cache = new HashMap<>();

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Visita visita = visitaMapper.map(rs, cache);
                visite.add(visita);
            }
        }

        return visite;
    }

    public void rimuoviById(Integer idVisita) throws SQLException {
        java.lang.String sql = "DELETE FROM visite WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVisita);

            stmt.executeUpdate();
        }
    }

    public void archiviaVisita(Integer idVisita) throws SQLException {
        String sql = """
                INSERT INTO archivio (
                    tipo_visita_id, titolo, descrizione, indirizzo_incontro, comune_incontro, provincia_incontro,
                    ora_inizio, durata_minuti, entrata_libera, luogo_nome, data_svolgimento, stato, username_volontario
                )
                SELECT tv.id, tv.titolo, tv.descrizione, tv.indirizzo_incontro, tv.comune_incontro, tv.provincia_incontro,
                       tv.ora_inizio, tv.durata_minuti, tv.entrata_libera, l.nome, v.data_svolgimento, ?, u.username
                FROM visite v
                JOIN tipi_visita tv ON v.tipo_visita_id = tv.id
                JOIN utenti u ON v.volontario_id = u.id
                JOIN luoghi l ON tv.luogo_id = l.id
                WHERE v.id = ?
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Visita.StatoVisita.EFFETTUATA.name());
            stmt.setInt(2, idVisita);
            stmt.executeUpdate();
        }
    }
}
