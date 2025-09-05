package com.unibs.mappers;

import com.unibs.models.Config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ConfigMapper implements RowMapper<Config> {

    @Override
    public Config map(ResultSet rs) throws SQLException {
        int numeroMaxIscrizioni = rs.getInt("numero_max_iscrizioni");
        java.sql.Date sqlDate = rs.getDate("periodo_corrente");
        LocalDate initializedOn = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        return new Config(null, numeroMaxIscrizioni, initializedOn);
    }
}