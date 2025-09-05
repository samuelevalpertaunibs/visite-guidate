package com.unibs.mappers;

import com.unibs.models.TipoVisitaCore;
import com.unibs.models.TipoVisitaPreview;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoVisitaPreviewMapper implements RowMapper<TipoVisitaPreview> {

    private final TipoVisitaCoreMapper coreMapper;

    public TipoVisitaPreviewMapper(TipoVisitaCoreMapper coreMapper) {
        this.coreMapper = coreMapper;
    }

    @Override
    public TipoVisitaPreview map(ResultSet rs) throws SQLException {
        TipoVisitaCore tvCore = coreMapper.map(rs);
        String luogoNome = rs.getString("luogo_nome");

        return new TipoVisitaPreview(tvCore, luogoNome);
    }
}
