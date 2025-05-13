package com.unibs.daos;

import com.unibs.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;

public class DatePrecluseDao {

    public void aggiungiDataPreclusa(LocalDate dataInserita) throws SQLException {
        String sql = "INSERT INTO date_precluse (data_preclusa) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dataInserita));
            stmt.executeUpdate();
        }
    }

    public boolean isDataPreclusa(LocalDate data) throws SQLException {
        String sql = "SELECT 1 FROM date_precluse WHERE data_preclusa = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(data));

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Se trova un risultato, la data è preclusa
            }

        }
    }

}
