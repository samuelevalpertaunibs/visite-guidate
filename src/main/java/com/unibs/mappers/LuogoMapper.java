package com.unibs.mappers;

import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LuogoMapper implements RowMapper<Luogo> {

    private final RowMapper<Comune> comuneMapper;

    public LuogoMapper(RowMapper<Comune> comuneMapper) {
        this.comuneMapper = comuneMapper;
    }

    @Override
    public Luogo map(ResultSet rs) throws SQLException {
        int id = rs.getInt("luogo_id");
        String nome = rs.getString("luogo_nome");
        String descrizione = rs.getString("luogo_descrizione");

        Comune comune = comuneMapper.map(rs);

        return new Luogo(id, nome, descrizione, comune);
    }
}