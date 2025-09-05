package com.unibs.mappers;

import com.unibs.models.CoppiaIdUsername;
import com.unibs.models.Utente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UtenteMapper implements RowMapper<Utente> {

    private final CoppiaIdUsernameMapper coppiaIdUsernameMapper;

    public UtenteMapper(CoppiaIdUsernameMapper coppiaIdUsernameMapper) {
        this.coppiaIdUsernameMapper = coppiaIdUsernameMapper;
    }

    @Override
    public Utente map(ResultSet rs) throws SQLException {
        CoppiaIdUsername coppiaIdUsername = this.coppiaIdUsernameMapper.map(rs);
        return new Utente(coppiaIdUsername, rs.getString("utente_password_hash"), rs.getBytes("utente_salt"), rs.getInt("utente_ruolo_id"), rs.getObject("utente_last_login", LocalDate.class));    }
}
