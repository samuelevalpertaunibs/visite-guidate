package com.unibs.mappers;

import com.unibs.models.CoppiaIdUsername;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CoppiaIdUsernameMapper implements RowMapper<CoppiaIdUsername> {

    @Override
    public CoppiaIdUsername map(ResultSet rs) throws SQLException {
        return new CoppiaIdUsername(rs.getInt("utente_id"), rs.getString("utente_username"));
    }
}