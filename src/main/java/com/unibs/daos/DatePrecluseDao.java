package com.unibs.daos;

import com.unibs.DatabaseException;
import com.unibs.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatePrecluseDao {


    public static void aggiungiDataPreclusa(LocalDate dataInserita) {
        String sql = "INSERT INTO date_precluse (data_preclusa) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(dataInserita));

            int affectedRows = stmt.executeUpdate();

             if (affectedRows == 0) {
            throw new DatabaseException("Data già esistente");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DatabaseException("Data già esistente");

        } catch (SQLException e) {
            throw new DatabaseException("Errore SQL: " + e.getMessage(), e);
        }
    }

    public static boolean isDataPreclusa(LocalDate data) {
        String sql = "SELECT 1 FROM date_precluse WHERE data_preclusa = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(data));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Se trova un risultato, la data è preclusa
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante la verifica della data: " + e.getMessage(), e);
        }
    }

    /**
     * Recupera tutte le date precluse dal database.
     *
     * @return Lista di date precluse
     * @throws DatabaseException Se si verifica un errore SQL
     */
    public static List<LocalDate> getDatePrecluse() {
        String sql = "SELECT data_preclusa FROM date_precluse";
        List<LocalDate> datePrecluse = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                datePrecluse.add(rs.getDate("data_preclusa").toLocalDate());
            }

        } catch (SQLException e) {
            throw new DatabaseException("Errore durante il recupero delle date precluse: " + e.getMessage(), e);
        }

        return datePrecluse;
    }
    public static boolean esistonoDatePrecluseNelMese(int anno, int mese) {
        String sql = "SELECT COUNT(*) FROM date_precluse WHERE YEAR(data_preclusa) = ? AND MONTH(data_preclusa) = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, anno);
            stmt.setInt(2, mese);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;  // Se il conteggio è maggiore di 0, esistono date
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
        return false;
    }
}
