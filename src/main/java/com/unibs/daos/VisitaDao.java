package com.unibs.daos;

import com.unibs.models.*;
import com.unibs.utils.DatabaseManager;
import com.unibs.utils.DateService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitaDao {

    /**
     * Ritorno una lista di Visite, i cui tipi di visita associati sono solo PARZIALMENTE inizializzati
     */
    public List<Visita> getVisitePreview(Visita.StatoVisita stato) throws SQLException {
        List<Visita> visite = new ArrayList<>();
        Map<Integer, TipoVisita> tipoVisitaCache = new HashMap<>();

        String sql = """
                                    SELECT
                                        v.id,
                                        v.tipo_visita_id,
                                        t.indirizzo_incontro,
                                        t.comune_incontro,
                                        t.provincia_incontro,
                                        t.titolo,
                                        t.descrizione,
                                        t.data_inizio,
                                        t.data_fine,
                                        t.ora_inizio,
                                        t.durata_minuti,
                                        t.entrata_libera,
                                        t.num_min_partecipanti,
                                        t.num_max_partecipanti,
                                        v.data_svolgimento,
                                        v.stato,
                                        u.username,
                                        l.nome
                                    FROM
                                        visite v
                                    JOIN tipi_visita t ON v.tipo_visita_id = t.id
                                    JOIN utenti u ON volontario_id = u.id
                                    JOIN luoghi l ON t.luogo_id = l.id
                                    WHERE
                                        v.stato = ?
                ORDER BY v.data_svolgimento
                """;

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int tipoVisitaId = rs.getInt("tipo_visita_id");
                String indirizzo = rs.getString("indirizzo_incontro");
                String comune = rs.getString("comune_incontro");
                String provincia = rs.getString("provincia_incontro");
                String titolo = rs.getString("titolo");
                String descrizione = rs.getString("descrizione");
                LocalDate dataInizio = rs.getDate("data_inizio").toLocalDate();
                LocalDate dataFine = rs.getDate("data_fine").toLocalDate();
                LocalTime oraInizio = rs.getTime("ora_inizio").toLocalTime();
                int durataMinuti = rs.getInt("durata_minuti");
                boolean entrataLibera = rs.getBoolean("entrata_libera");
                int numMinPartecipanti = rs.getInt("num_min_partecipanti");
                int numMaxPartecipanti = rs.getInt("num_max_partecipanti");
                LocalDate dataSvolgimento = rs.getDate("data_svolgimento").toLocalDate();
                Visita.StatoVisita statoVisita = Visita.StatoVisita.valueOf(rs.getString("stato"));
                String nomeVolontario = rs.getString("username");
                String luogoNome = rs.getString("nome");
                Integer visitaId = rs.getInt("id");

                // Utilizzo una cache per evitare di creare un nuovo tipo visita per ogni istanza di visita
                TipoVisita tipoVisita = tipoVisitaCache.get(tipoVisitaId);
                if (tipoVisita == null) {
                    PuntoIncontro puntoIncontro = new PuntoIncontro(indirizzo, comune, provincia);
                    Luogo luogo = new Luogo(null, luogoNome, null, null);
                    tipoVisita = new TipoVisita(tipoVisitaId, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, luogo, puntoIncontro, null, null);
                    tipoVisitaCache.put(tipoVisitaId, tipoVisita);
                }

                visite.add(new Visita(visitaId, tipoVisita, dataSvolgimento, new Volontario(null, nomeVolontario, null, null, null), statoVisita));
            }
        }
        return visite;
    }

    public void inserisciVisite(List<Visita> tutteLeVisite) throws SQLException {
        String sql = "INSERT INTO visite (tipo_visita_id, volontario_id, data_svolgimento, stato) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Visita visita : tutteLeVisite) {
                stmt.setInt(1, visita.getTipoVisita().id());
                stmt.setInt(2, visita.getVolontario().getId());
                stmt.setDate(3, Date.valueOf(visita.getDataSvolgimento()));
                stmt.setString(4, visita.getStato().name());
                stmt.addBatch();
            }

            // Eseguo tutte le insert una sola volta assieme
            stmt.executeBatch();
        }
    }

    @SuppressWarnings("SqlWithoutWhere")
    public void rimuoviDisponibilita() throws SQLException {
        String sql = "DELETE FROM disponibilita";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate(); // Esegue direttamente la DELETE
        }
    }

    public Integer iscrivi(Integer utenteId, Integer visitaSelezionataId, Integer numeroIscritti) throws SQLException {
        String sql = "INSERT INTO iscrizioni (fruitore_id, visita_id, numero_iscritti) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
                SELECT tv.num_max_partecipanti - COALESCE(SUM(i.numero_iscritti), 0) AS posti_disponibili
                FROM visite v
                    INNER JOIN tipi_visita tv ON v.tipo_visita_id = tv.id
                    LEFT JOIN iscrizioni i ON v.id = i.visita_id
                WHERE v.id = ? GROUP BY tv.num_max_partecipanti;
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitaId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("posti_disponibili");
                }
            }
        }
        return 0;
    }

    public void setStatoById(Integer visitaId, String stato) throws SQLException {
        String sql = "UPDATE visite SET stato = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stato);
            stmt.setInt(2, visitaId);
            stmt.executeUpdate();
        }
    }

    public List<Visita> getVisitePreviewByFruitore(Visita.StatoVisita stato, String nomeFruitoreIscritto) throws SQLException {
        List<Visita> visite = new ArrayList<>();
        Map<Integer, TipoVisita> tipoVisitaCache = new HashMap<>();

        String sql = """
                SELECT DISTINCT
                    v.id,
                    v.tipo_visita_id,
                    t.indirizzo_incontro,
                    t.comune_incontro,
                    t.provincia_incontro,
                    t.titolo,
                    t.descrizione,
                    t.data_inizio,
                    t.data_fine,
                    t.ora_inizio,
                    t.durata_minuti,
                    t.entrata_libera,
                    t.num_min_partecipanti,
                    t.num_max_partecipanti,
                    v.data_svolgimento,
                    v.stato,
                    u.username AS volontario_username,
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

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            stmt.setString(2, nomeFruitoreIscritto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int tipoVisitaId = rs.getInt("tipo_visita_id");
                    String indirizzo = rs.getString("indirizzo_incontro");
                    String comune = rs.getString("comune_incontro");
                    String provincia = rs.getString("provincia_incontro");
                    String titolo = rs.getString("titolo");
                    String descrizione = rs.getString("descrizione");
                    LocalDate dataInizio = rs.getDate("data_inizio").toLocalDate();
                    LocalDate dataFine = rs.getDate("data_fine").toLocalDate();
                    LocalTime oraInizio = rs.getTime("ora_inizio").toLocalTime();
                    int durataMinuti = rs.getInt("durata_minuti");
                    boolean entrataLibera = rs.getBoolean("entrata_libera");
                    int numMinPartecipanti = rs.getInt("num_min_partecipanti");
                    int numMaxPartecipanti = rs.getInt("num_max_partecipanti");
                    LocalDate dataSvolgimento = rs.getDate("data_svolgimento").toLocalDate();
                    Visita.StatoVisita statoVisita = Visita.StatoVisita.valueOf(rs.getString("stato"));
                    String nomeVolontario = rs.getString("volontario_username");
                    String luogoNome = rs.getString("luogo_nome");
                    Integer id = rs.getInt("id");

                    TipoVisita tipoVisita = tipoVisitaCache.get(tipoVisitaId);
                    if (tipoVisita == null) {
                        PuntoIncontro puntoIncontro = new PuntoIncontro(indirizzo, comune, provincia);
                        Luogo luogo = new Luogo(null, luogoNome, null, null);
                        tipoVisita = new TipoVisita(tipoVisitaId, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, luogo, puntoIncontro, null, null);
                        tipoVisitaCache.put(tipoVisitaId, tipoVisita);
                    }

                    Visita visita = new Visita(id, tipoVisita, dataSvolgimento, new Volontario(null, nomeVolontario, null, null, null), statoVisita);
                    visite.add(visita);
                }
            }
        }

        return visite;
    }

    public List<String> getCodiciPrenotazioneFruitorePerVista(String nomeFruitore, Integer visitaId) throws SQLException {
        String sql = "SELECT i.id FROM iscrizioni i " +
                "JOIN utenti u ON fruitore_id = u.id " +
                "WHERE u.username = ? AND visita_id = ?";

        List<String> codiciPrenotazione = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT fruitore_id FROM iscrizioni WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codiceIscrizione);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("fruitore_id");
            }
        }
        return null;
    }

    public Integer getIdVisitaByIdIscrizione(Integer codiceIscrizione) throws SQLException {
        String sql = "SELECT visita_id FROM iscrizioni WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codiceIscrizione);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("visita_id");
            }
        }
        return null;
    }

    public void disdiciIscrizione(Integer codiceIscrizione) throws SQLException {
        String sql = "DELETE FROM iscrizioni WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codiceIscrizione);
            stmt.executeUpdate();
        }
    }

    public Visita.StatoVisita getStatoById(Integer visitaId) throws SQLException {
        String sql = "SELECT stato FROM visite WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitaId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Visita.StatoVisita.valueOf(rs.getString("stato"));
            }
        }
        return null;
    }

    public List<Visita> getVisitePreviewByVolontario(Visita.StatoVisita stato, String nomeVolontario) throws SQLException {
        List<Visita> visite = new ArrayList<>();
        Map<Integer, TipoVisita> tipoVisitaCache = new HashMap<>();

        String sql = """
                SELECT DISTINCT
                                    v.id,
                                    v.tipo_visita_id,
                                    t.indirizzo_incontro,
                                    t.comune_incontro,
                                    t.provincia_incontro,
                                    t.titolo,
                                    t.descrizione,
                                    t.data_inizio,
                                    t.data_fine,
                                    t.ora_inizio,
                                    t.durata_minuti,
                                    t.entrata_libera,
                                    t.num_min_partecipanti,
                                    t.num_max_partecipanti,
                                    v.data_svolgimento,
                                    v.stato,
                                    l.nome AS luogo_nome
                                FROM visite v
                                    JOIN tipi_visita t ON v.tipo_visita_id = t.id
                                    JOIN utenti u ON v.volontario_id = u.id
                                    JOIN luoghi l ON t.luogo_id = l.id
                                WHERE v.stato = ? AND u.username = ?
                                ORDER BY v.data_svolgimento
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stato.name());
            stmt.setString(2, nomeVolontario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int tipoVisitaId = rs.getInt("tipo_visita_id");
                    String indirizzo = rs.getString("indirizzo_incontro");
                    String comune = rs.getString("comune_incontro");
                    String provincia = rs.getString("provincia_incontro");
                    String titolo = rs.getString("titolo");
                    String descrizione = rs.getString("descrizione");
                    LocalDate dataInizio = rs.getDate("data_inizio").toLocalDate();
                    LocalDate dataFine = rs.getDate("data_fine").toLocalDate();
                    LocalTime oraInizio = rs.getTime("ora_inizio").toLocalTime();
                    int durataMinuti = rs.getInt("durata_minuti");
                    boolean entrataLibera = rs.getBoolean("entrata_libera");
                    int numMinPartecipanti = rs.getInt("num_min_partecipanti");
                    int numMaxPartecipanti = rs.getInt("num_max_partecipanti");
                    LocalDate dataSvolgimento = rs.getDate("data_svolgimento").toLocalDate();
                    Visita.StatoVisita statoVisita = Visita.StatoVisita.valueOf(rs.getString("stato"));
                    String luogoNome = rs.getString("luogo_nome");
                    Integer id = rs.getInt("id");

                    TipoVisita tipoVisita = tipoVisitaCache.get(tipoVisitaId);
                    if (tipoVisita == null) {
                        PuntoIncontro puntoIncontro = new PuntoIncontro(indirizzo, comune, provincia);
                        Luogo luogo = new Luogo(null, luogoNome, null, null);
                        tipoVisita = new TipoVisita(tipoVisitaId, titolo, descrizione, dataInizio, dataFine, oraInizio, durataMinuti, entrataLibera, numMinPartecipanti, numMaxPartecipanti, luogo, puntoIncontro, null, null);
                        tipoVisitaCache.put(tipoVisitaId, tipoVisita);
                    }

                    Visita visita = new Visita(id, tipoVisita, dataSvolgimento, new Volontario(null, nomeVolontario, null, null, null), statoVisita);
                    visite.add(visita);
                }
            }
        }

        return visite;
    }

    public List<String> getCodiciPrenotazionePerVista(String username, Integer id) throws SQLException {
        String sql = """
                SELECT i.id as iscrizione_id, i.numero_iscritti as numero_iscritti FROM iscrizioni i
                JOIN visite v ON i.visita_id = v.id
                WHERE v.volontario_id = ?
                AND v.id = ?
                """;

        List<String> codiciPrenotazione = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        String sql = "SELECT id FROM visite WHERE stato = 'COMPLETA' AND data_svolgimento <= ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(DateService.today().plusDays(3)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                idVisite.add(rs.getInt("id"));
            }
        }

        return idVisite;
    }

    public List<Integer> getIdVisiteDaFare() throws SQLException {
        List<Integer> idVisite = new ArrayList<>();
        String sql = """
                SELECT v.id FROM visite v JOIN tipi_visita tv ON tv.id = v.tipo_visita_id WHERE v.stato = 'PROPOSTA' AND v.data_svolgimento <= ? AND (SELECT SUM(iscrizioni.numero_iscritti) FROM iscrizioni WHERE iscrizioni.visita_id = v.id) >= tv.num_min_partecipanti
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(DateService.today().plusDays(3)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                idVisite.add(rs.getInt("id"));
            }
        }

        return idVisite;
    }

    public List<Integer> getIdVisiteDaCancellare() throws SQLException {
        List<Integer> idVisite = new ArrayList<>();
        String sql = """
                    SELECT v.id
                                               FROM visite v
                                               JOIN tipi_visita tv ON tv.id = v.tipo_visita_id
                                               WHERE v.stato = 'PROPOSTA'
                                                 AND v.data_svolgimento <= ?
                                                 AND (
                                                   SELECT COALESCE(SUM(i.numero_iscritti), 0)
                                                   FROM iscrizioni i
                                                   WHERE i.visita_id = v.id
                                                 ) < tv.num_min_partecipanti
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(DateService.today().plusDays(3)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                idVisite.add(rs.getInt("id"));
            }
        }

        return idVisite;
    }

    public List<Integer> getIdVisiteDaRendereEffettuate() throws SQLException {
        List<Integer> idVisite = new ArrayList<>();
        String sql = "SELECT v.id FROM visite v WHERE v.stato = 'CONFERMATA' AND v.data_svolgimento < ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(DateService.today()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                idVisite.add(rs.getInt("id"));
            }
        }

        return idVisite;
    }

    public void rimuoviVisiteCancellate() throws SQLException {
        String sql = "DELETE FROM visite WHERE stato = 'CANCELLATA' AND data_svolgimento < ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(DateService.today()));

            stmt.executeUpdate();
        }
    }

    // TODO
    public List<Visita> getVisiteFromArchivio() {
        String sql = "SELECT * FROM archivio_storico";
        return null;
    }

    public void rimuoviById(Integer idVisita) throws SQLException {
        String sql = "DELETE FROM visite WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVisita);

            stmt.executeUpdate();
        }
    }

    public void archiviaVisita(Integer idVisita) throws SQLException {
        String sql = "INSERT INTO archivio (" +
                "titolo, descrizione, indirizzo_incontro, comune_incontro, provincia_incontro, " +
                "ora_inizio, durata_minuti, entrata_libera, luogo_nome, data_svolgimento, stato, username_volontario" +
                ") " +
                "SELECT tv.titolo, tv.descrizione, tv.indirizzo_incontro, tv.comune_incontro, tv.provincia_incontro, " +
                "tv.ora_inizio, tv.durata_minuti, tv.entrata_libera, l.nome, v.data_svolgimento, ?, u.username " +
                "FROM visite v " +
                "JOIN tipi_visita tv ON v.tipo_visita_id = tv.id " +
                "JOIN utenti u ON v.volontario_id = u.id " +
                "JOIN luoghi l ON tv.luogo_id = l.id " +
                "WHERE v.id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, Visita.StatoVisita.EFFETTUATA.name());
            stmt.setInt(2, idVisita);
            stmt.executeUpdate();
        }
    }
}
