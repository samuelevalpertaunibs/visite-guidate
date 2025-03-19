package com.unibs.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;

public class TipoVisitaDao {

    public static void aggiungiVisita(String titolo, String descrizione, LocalDate dataInizio, LocalDate dataFine,
            LocalTime oraInizio, int durataMinuti, boolean entrataLiberaBool, int numeroMin, int numeroMax,
            String nomeLuogoSelezionato, String[] volontari) {

        String insertSql = "INSERT INTO tipo_visita (titolo, descrizione, data_inizio, data_fine, ora_inizio, durata_minuti, entrata_libera, num_min_partecipanti, num_max_partecipanti, luogo_fk) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        String insertLuogoVisitaNN = "INSERT INTO tipovisita_luogo_nn (tipo_visita_fk, luogo_fk) VALUES (?, ?)";

        String insertVolontarioNN = "INSERT INTO tipovisita_volontario_nn (tipo_visita_fk, volontario_fk) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); // Inizio della transazione

            int tipoVisitaId = -1; // Per salvare l'ID della visita appena inserita

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
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        tipoVisitaId = rs.getInt(1);
                    } else {
                        throw new DatabaseException("Errore nel recupero dell'ID generato");
                    }
                }
            }

            // Inserisce l'associazione tra visita e luogo
            try (PreparedStatement stmt2 = conn.prepareStatement(insertLuogoVisitaNN)) {
                stmt2.setInt(1, tipoVisitaId);
                stmt2.setString(2, nomeLuogoSelezionato);

                int affectedRows = stmt2.executeUpdate();
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

            conn.commit(); // Se tutto va bene conferma la transazione
        } catch (Exception e) {
            try {
                DatabaseManager.getConnection().rollback(); // Se qualcosa va storto rimuovi tutto
            } catch (Exception rollbackEx) {
                throw new DatabaseException("Errore nel rollback: " + rollbackEx.getMessage());
            }
            throw new DatabaseException("Errore nella transazione: " + e.getMessage());
        } finally {
            try {
                DatabaseManager.getConnection().setAutoCommit(true); // Ripristina auto-commit
            } catch (Exception autoCommitEx) {
                throw new DatabaseException("Errore nel ripristino dell'auto-commit: " + autoCommitEx.getMessage());
            }
        }
    }
}
