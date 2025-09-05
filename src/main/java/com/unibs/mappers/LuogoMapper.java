package com.unibs.mappers;

import com.unibs.models.Comune;
import com.unibs.models.Luogo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class LuogoMapper implements RowMapper<Luogo> {

    private final ComuneMapper comuneMapper;

    public LuogoMapper(ComuneMapper comuneMapper) {
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

    public Luogo map(Map<String, Object> dataMap) {
        int id = ((Number) dataMap.get("luogo_id")).intValue();
        String nome = (String) dataMap.get("luogo_nome");
        String descrizione = (String) dataMap.get("luogo_descrizione");

        Comune comune = comuneMapper.map(dataMap);

        return new Luogo(id, nome, descrizione, comune);
    }
}