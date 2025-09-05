package com.unibs.mappers;

import com.unibs.models.Comune;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ComuneMapper implements RowMapper<Comune> {

    @Override
    public Comune map(ResultSet rs) throws SQLException {
        int id = rs.getInt("comune_id");
        String nome = rs.getString("comune_nome");
        String provincia = rs.getString("comune_provincia");
        String regione = rs.getString("comune_regione");
        return new Comune(id, nome, provincia, regione);
    }

    public Comune map(Map<String, Object> dataMap) {
        int id = ((Number) dataMap.get("comune_id")).intValue();
        String nome = (String) dataMap.get("comune_nome");
        String provincia = (String) dataMap.get("comune_provincia");
        String regione = (String) dataMap.get("comune_regione");
        return new Comune(id, nome, provincia, regione);
    }
}