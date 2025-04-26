package com.unibs.daos;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Comune;
import com.unibs.models.Luogo;
import com.unibs.models.PuntoIncontro;
import com.unibs.models.TipoVisita;

public class TipoVisitaDao {

    public static void aggiungiVisita(String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine,
                                      LocalTime oraInizio, int durataMinuti, boolean entrataLiberaBool, int numeroMin, int numeroMax,
                                      Luogo luogoDaAssociare, int[] volontariIds, int[] giorniIds, String indirizzoPuntoIncontro, String comunePuntoIncontro, String provinciaPuntoIncontro) {

        String insertSql = "INSERT INTO tipi_visita (titolo, descrizione ,data_inizio, data_fine, ora_inizio, durata_minuti, entrata_libera, num_min_partecipanti, num_max_partecipanti, luogo_id, indirizzo_incontro, comune_incontro, provincia_incontro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertVolontarioNN = "INSERT INTO tipi_visita_volontari (tipo_visita_id, volontario_id) VALUES (?, ?)";

        String insertGiorniNN = "INSERT INTO giorni_settimana_tipi_visita (tipo_visita_id, giorno_settimana_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Disabilita auto-commit all'inizio

            int tipoVisitaId; // Per salvare l'ID della visita appena inserita

            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, titolo);
                stmt.setString(2, descrizione);
                stmt.setDate(3, java.sql.Date.valueOf(dataInizio));
                stmt.setDate(4, java.sql.Date.valueOf(dataFine));
                stmt.setTime(5, java.sql.Time.valueOf(oraInizio));
                stmt.setInt(6, durataMinuti);
                stmt.setBoolean(7, entrataLiberaBool);
                stmt.setInt(8, numeroMin);
                stmt.setInt(9, numeroMax);
                stmt.setInt(10, luogoDaAssociare.getId());
                stmt.setString(11, indirizzoPuntoIncontro);
                stmt.setString(12, comunePuntoIncontro);
                stmt.setString(13, provinciaPuntoIncontro);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new DatabaseException("Inserimento tipo visita fallito");
                }

                // Ottiene l'ID generato
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    tipoVisitaId = rs.getInt(1);
                } else {
                    throw new DatabaseException("Errore nel recupero dell'ID generato.");
                }

                // Inserisce ogni volontario nella tabella tipovisita_volontario_nn
                try (PreparedStatement stmt3 = conn.prepareStatement(insertVolontarioNN)) {
                    for (int volontarioId : volontariIds) {
                        stmt3.setInt(1, tipoVisitaId);
                        stmt3.setInt(2, volontarioId);
                        stmt3.addBatch(); // Aggiunge alla batch per eseguire più query in un solo colpo
                    }
                    int[] batchResults = stmt3.executeBatch(); // Esegue tutte le insert insieme

                    // Controlla se almeno un'operazione è fallita
                    for (int result : batchResults) {
                        if (result == Statement.EXECUTE_FAILED) {
                            throw new DatabaseException("Inserimento volontario fallito.");
                        }
                    }
                }

                // Inserisce ogni giorno della settimana nella tabella giornosettimana_tipovisita_nn
                try (PreparedStatement stmt4 = conn.prepareStatement(insertGiorniNN)) {
                    for (int giornoId : giorniIds) {
                        stmt4.setInt(1, tipoVisitaId);
                        stmt4.setInt(2, giornoId);
                        stmt4.addBatch(); // Aggiunge alla batch per eseguire più query in un solo colpo
                    }
                    int[] batchResults = stmt4.executeBatch(); // Esegue tutte le insert insieme

                    // Controlla se almeno un'operazione è fallita
                    for (int result : batchResults) {
                        if (result == Statement.EXECUTE_FAILED) {
                            throw new DatabaseException("Inserimento giorni fallito.");
                        }
                    }
                }

                conn.commit(); // Se tutto va bene conferma la transazione
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Esegui il rollback se qualcosa va storto
                }
            } catch (Exception rollbackEx) {
                throw new DatabaseException("Errore nel rollback: " + rollbackEx.getMessage());
            }
            if ("23000".equals(e.getSQLState()) && e.getErrorCode() == 1062) {
                // Unique non rispettata
                throw new DatabaseException("Assicurati che il punto di incontro sia univoco");
            }
            throw new DatabaseException("Errore nella transazione: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Ripristina auto-commit
                }
            } catch (Exception autoCommitEx) {
                throw new DatabaseException("Errore nel ripristino dell'auto-commit: " + autoCommitEx.getMessage());
            }
        }
    }

    public static boolean isEmpty() {
        String sql = "SELECT COUNT(*) FROM tipi_visita";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il controllo della tabella tipo_visita: " + e.getMessage());
        }

        return true;
    }

    public static List<String> getTitoliByVolontarioId(int volontarioId) {
        List<String> titoliTipiVisita = new ArrayList<>();
        String sql = "SELECT titolo FROM tipi_visita JOIN tipi_visita_volontari ON tipi_visita.id = tipi_visita_volontari.tipo_visita_id WHERE volontario_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, volontarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                titoliTipiVisita.add(rs.getString("titolo"));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel recupero dei tipi di visita associati al volontario: " + e.getMessage());
        }
        return titoliTipiVisita;
    }

    public static List<String> getTitoliByLuogoId(int luogoId) {
        List<String> titoliTipiVisita = new ArrayList<>();
        String sql = "SELECT titolo FROM tipi_visita WHERE luogo_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, luogoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                titoliTipiVisita.add(rs.getString("titolo"));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel recupero dei tipi di visita associati al luogo: " + e.getMessage());
        }
        return titoliTipiVisita;
    }

    public static boolean siSovrappone(int luogoId, int[] giorniIds, LocalTime oraInizio, int durataMinuti, LocalDate dataInizio, LocalDate dataFine) {
        String giorniIdsPlaceholders = String.join(",",
                java.util.Collections.nCopies(giorniIds.length, "?")
        );

        String sql = String.format("""
                SELECT COUNT(*)
                FROM tipi_visita
                JOIN luoghi ON tipi_visita.luogo_id = luoghi.id
                JOIN giorni_settimana_tipi_visita ON giorni_settimana_tipi_visita.tipo_visita_id = tipi_visita.id
                JOIN giorni_settimana ON giorni_settimana_tipi_visita.giorno_settimana_id = giorni_settimana.id
                WHERE luoghi.id = ?
                AND (
                    (tipi_visita.data_inizio < ? AND ? < tipi_visita.data_fine)
                    OR
                    (tipi_visita.data_inizio < ? AND ? < tipi_visita.data_fine)
                )
                AND (
                    (tipi_visita.ora_inizio < ? AND ? < DATE_ADD(ora_inizio, INTERVAL durata_minuti MINUTE))
                    OR
                    (tipi_visita.ora_inizio < ? AND ? < DATE_ADD(ora_inizio, INTERVAL durata_minuti MINUTE))
                )
                AND giorni_settimana.id IN (%s)
                """, giorniIdsPlaceholders);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, luogoId);
            stmt.setDate(2,  Date.valueOf(dataInizio));
            stmt.setDate(3,  Date.valueOf(dataInizio));
            stmt.setDate(4,  Date.valueOf(dataFine));
            stmt.setDate(5,  Date.valueOf(dataFine));
            stmt.setTime(6,  Time.valueOf(oraInizio));
            stmt.setTime(7,  Time.valueOf(oraInizio));
            Time oraFine = Time.valueOf(oraInizio.plusMinutes(durataMinuti));
            stmt.setTime(8,  oraFine);
            stmt.setTime(9,  oraFine);

            for (int i = 0; i < giorniIds.length; i++) {
                stmt.setInt(i + 10, giorniIds[i]);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                    return rs.getInt(1) > 0;

            throw new DatabaseException("Errore nel controllo sulla sovrapposizione");

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel controllo sulla sovrapposizione: " + e.getMessage());
        }
    }

    public static boolean existsByTitle(String titolo) {
        String sql = "SELECT COUNT(*) FROM tipi_visita WHERE titolo = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setString(1, titolo);
             ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il controllo della tabella tipo_visita: " + e.getMessage());
        }

        return false;
    }

    public static ArrayList<String> getPreviewTipiVisita() {
        String sql = "SELECT titolo, nome FROM tipi_visita JOIN luoghi ON tipi_visita.luogo_id = luoghi.id";
        ArrayList<String> tipiVisita = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tipiVisita.add(rs.getString(1) + " presso " + rs.getString(2));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il controllo della tabella tipo_visita: " + e.getMessage());
        }

        return tipiVisita;
    }

    public static TipoVisita getVisitaByTitolo(String titolo) {
        String sql = """
        SELECT tv.id, tv.titolo, tv.descrizione, tv.data_inizio, tv.data_fine, tv.ora_inizio,
               tv.durata_minuti, tv.entrata_libera, tv.num_min_partecipanti, tv.num_max_partecipanti,
               l.id AS luogo_id, l.nome AS luogo_nome, l.descrizione AS luogo_descrizione,
               c.id AS comune_id, c.nome AS comune_nome, c.provincia AS comune_provincia, c.regione AS comune_regione,
               tv.indirizzo_incontro, tv.comune_incontro, tv.provincia_incontro
        FROM tipi_visita tv
        JOIN luoghi l ON tv.luogo_id = l.id
        JOIN comuni c ON l.comune_id = c.id
        WHERE tv.titolo = ?
    """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titolo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Comune comune = new Comune(
                        rs.getInt("comune_id"),
                        rs.getString("comune_nome"),
                        rs.getString("comune_provincia"),
                        rs.getString("comune_regione")
                        );

                Luogo luogo = new Luogo(
                        rs.getInt("luogo_id"),
                        rs.getString("luogo_nome"),
                        rs.getString("luogo_descrizione"),
                        comune
                );

                PuntoIncontro puntoIncontro = new PuntoIncontro(
                        rs.getString("indirizzo_incontro"),
                        rs.getString("comune_incontro"),
                        rs.getString("provincia_incontro")
                );

                return new TipoVisita(
                        rs.getInt("id"),
                        rs.getString("titolo"),
                        rs.getString("descrizione"),
                        rs.getDate("data_inizio").toLocalDate(),
                        rs.getDate("data_fine").toLocalDate(),
                        rs.getTime("ora_inizio").toLocalTime(),
                        rs.getInt("durata_minuti"),
                        rs.getBoolean("entrata_libera"),
                        rs.getInt("num_min_partecipanti"),
                        rs.getInt("num_max_partecipanti"),
                        luogo,
                        puntoIncontro
                );
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore nel recupero del tipo visita con titolo: " + titolo + " - " + e.getMessage());
        }
    }

//    public static ArrayList<TipoVisita> getByVolontarioIdAndMese(int volontarioId, int mese) {
//        ArrayList<TipoVisita> ids = new ArrayList<>();
//        String query = """
//                SELECT tv.id FROM tipi_visita tv
//                JOIN tipi_visita_volontari nn  ON tv.id = nn.tipo_visita_id
//                WHERE nn.volontario_id = ?
//                AND MONTH(tv.data_inizio) <= ?
//                AND MONTH(tv.data_fine) >= ?
//               """;
//
//        try (Connection conn = DatabaseManager.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            stmt.setInt(1, volontarioId);
//            stmt.setInt(2, mese);
//            stmt.setInt(3, mese);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                int id = rs.getInt("id");
//                ids.add(id);
//            }
//
//            return ids.toArray(new Integer[0]);
//
//        } catch (SQLException e) {
//            throw new DatabaseException("Errore nel recupero dei tipi visita associati al volontario: " + volontarioId + " - " + e.getMessage());
//        }
//    }
}
