package com.unibs.daos;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;

public class TipoVisitaDao {

    public static void aggiungiVisita(String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine,
                                      LocalTime oraInizio, int durataMinuti, boolean entrataLiberaBool, int numeroMin, int numeroMax,
                                      String nomeLuogoSelezionato, String[] volontari, String[] giorni) {

        String insertSql = "INSERT INTO tipi_visita (titolo, descrizione, data_inizio, data_fine, ora_inizio, durata_minuti, entrata_libera, num_min_partecipanti, num_max_partecipanti, luogo_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        String insertLuogoVisitaNN = "INSERT INTO tipi_visita_luoghi (tipo_visita_id, luogo_id) VALUES (?, ?)";

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
                stmt.setString(10, nomeLuogoSelezionato);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new DatabaseException("Inserimento tipo visita fallito");
                }

                // Ottiene l'ID generato
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    tipoVisitaId = rs.getInt(1);
                } else {
                    throw new DatabaseException("Errore nel recupero dell'ID generato");

                }

                // Inserisce l'associazione tra visita e luogo
                try (PreparedStatement stmt2 = conn.prepareStatement(insertLuogoVisitaNN)) {
                    stmt2.setInt(1, tipoVisitaId);
                    stmt2.setString(2, nomeLuogoSelezionato);

                    affectedRows = stmt2.executeUpdate();
                    if (affectedRows == 0) {
                        throw new DatabaseException("Associazione tra luogo e tipo visita fallita");
                    }
                }

                // Inserisce ogni volontario nella tabella tipovisita_volontario_nn
                try (PreparedStatement stmt3 = conn.prepareStatement(insertVolontarioNN)) {
                    for (String volontario : volontari) {
                        stmt3.setInt(1, tipoVisitaId);
                        stmt3.setString(2, volontario);
                        stmt3.addBatch(); // Aggiunge alla batch per eseguire più query in un solo colpo
                    }
                    int[] batchResults = stmt3.executeBatch(); // Esegue tutte le insert insieme

                    // Controlla se almeno un'operazione è fallita
                    for (int result : batchResults) {
                        if (result == Statement.EXECUTE_FAILED) {
                            throw new DatabaseException("Inserimento volontario fallito");
                        }
                    }
                }

                // Inserisce ogni giorno della settimana nella tabella giornosettimana_tipovisita_nn
                try (PreparedStatement stmt4 = conn.prepareStatement(insertGiorniNN)) {
                    for (String giorno : giorni) {
                        stmt4.setInt(1, tipoVisitaId);
                        stmt4.setString(2, giorno);
                        stmt4.addBatch(); // Aggiunge alla batch per eseguire più query in un solo colpo
                    }
                    int[] batchResults = stmt4.executeBatch(); // Esegue tutte le insert insieme

                    // Controlla se almeno un'operazione è fallita
                    for (int result : batchResults) {
                        if (result == Statement.EXECUTE_FAILED) {
                            throw new DatabaseException("Inserimento giorni fallito");
                        }
                    }
                }

                conn.commit(); // Se tutto va bene conferma la transazione

            }
        } catch (Exception e) {
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
}
