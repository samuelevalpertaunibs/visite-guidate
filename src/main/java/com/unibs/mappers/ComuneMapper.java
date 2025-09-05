package com.unibs.mappers;

import com.unibs.models.Comune;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComuneMapper implements RowMapper<Comune> {

    @Override
    public Comune map(ResultSet rs) throws SQLException {
        int id = rs.getInt("comune_id");
        String nome = rs.getString("comune_nome");
        String provincia = rs.getString("comune_provincia");
        String regione = rs.getString("comune_regione");
        return new Comune(id, nome, provincia, regione);
    }
}