package com.unibs.daos;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;
import com.unibs.models.Luogo;

public class TipoVisitaDao {

    public static void aggiungiVisita(String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine,
                                      LocalTime oraInizio, int durataMinuti, boolean entrataLiberaBool, int numeroMin, int numeroMax,
                                      Luogo luogoDaAssociare, int[] volontariIds, int[] giorniIds, String indirizzoPuntoIncontro) {

        String insertSql = "INSERT INTO tipi_visita (titolo, descrizione ,data_inizio, data_fine, ora_inizio, durata_minuti, entrata_libera, num_min_partecipanti, num_max_partecipanti, luogo_id, punto_incontro_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insertLuogoVisitaNN = "INSERT INTO tipi_visita_luoghi (tipo_visita_id, luogo_id) VALUES (?, ?)";

        String insertVolontarioNN = "INSERT INTO tipi_visita_volontari (tipo_visita_id, volontario_id) VALUES (?, ?)";

        String insertGiorniNN = "INSERT INTO giorni_settimana_tipi_visita (tipo_visita_id, giorno_settimana_id) VALUES (?, ?)";

        String insertPuntoIncontro = "INSERT INTO punti_incontro (indirizzo, comune_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // Disabilita auto-commit all'inizio

            int tipoVisitaId; // Per salvare l'ID della visita appena inserita
            int puntoIncontroId;

            try(PreparedStatement stmt = conn.prepareStatement(insertPuntoIncontro, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, indirizzoPuntoIncontro);
                stmt.setInt(2, luogoDaAssociare.getIdComune());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new DatabaseException("Inserimento punto di incontro fallito");
                }

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    puntoIncontroId = rs.getInt(1);
                } else {
                    throw new DatabaseException("Errore nel recupero dell'ID generato.");
                }
            } catch (SQLException e) {
                throw new DatabaseException("Impossibile inserire il punto di incontro, assicurati che sia univoco.");
            }

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
                stmt.setInt(11, puntoIncontroId);

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

                // Inserisce l'associazione tra visita e luogo
                try (PreparedStatement stmt2 = conn.prepareStatement(insertLuogoVisitaNN)) {
                    stmt2.setInt(1, tipoVisitaId);
                    stmt2.setInt(2, luogoDaAssociare.getId());

                    affectedRows = stmt2.executeUpdate();
                    if (affectedRows == 0) {
                        throw new DatabaseException("Associazione tra luogo e tipo visita fallita.");
                    }
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

    public static ArrayList<String> getTitoli() {
        ArrayList<String> tipiVisita = new ArrayList<>();
        String query = "SELECT titolo FROM tipi_visita";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tipiVisita.add(rs.getString("titolo"));
            }
        } catch (Exception e) {
            throw new DatabaseException("Errore nel recupero dei tipi di visita: " + e.getMessage());
        }
        return tipiVisita;
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
}
