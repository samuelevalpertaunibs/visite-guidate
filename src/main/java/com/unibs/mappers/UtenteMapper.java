package com.unibs.mappers;

import com.unibs.models.Utente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UtenteMapper implements RowMapper<Utente> {

    @Override
    public Utente map(ResultSet rs) throws SQLException {
        return new Utente(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"), rs.getBytes("salt"), rs.getInt("ruolo_id"), rs.getObject("last_login", LocalDate.class));
    }
}
