package com.unibs.daos;

import com.unibs.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

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

    public Set<LocalDate> findByMonth(YearMonth mese) throws SQLException {
        Set<LocalDate> date = new HashSet<>();

        String sql = "SELECT data_preclusa FROM date_precluse WHERE data_preclusa BETWEEN ? AND ?";

        LocalDate start = mese.atDay(1);
        LocalDate end = mese.atEndOfMonth();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    date.add(rs.getDate("data_preclusa").toLocalDate());
                }
            }

        }

        return date;
    }

}
