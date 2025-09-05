package com.unibs.mappers;

import com.unibs.models.TipoVisitaCore;
import com.unibs.models.TipoVisitaPreview;
import com.unibs.models.Visita;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

public class VisitaMapper implements CacheMapper<Visita, Integer, TipoVisitaCore> {

    private final TipoVisitaMapper tipoVisitaMapper;
    private final TipoVisitaPreviewMapper tipoVisitaPreviewMapper;
    private final CoppiaIdUsernameMapper coppiaIdUsernameMapper;


    public VisitaMapper(TipoVisitaMapper tipoVisitaMapper, TipoVisitaPreviewMapper tipoVisitaPreviewMapper, CoppiaIdUsernameMapper coppiaIdUsernameMapper) {
        this.tipoVisitaMapper = tipoVisitaMapper;
        this.tipoVisitaPreviewMapper = tipoVisitaPreviewMapper;
        this.coppiaIdUsernameMapper = coppiaIdUsernameMapper;
    }

    public Visita map(ResultSet rs, Map<Integer, TipoVisitaCore> cache) throws SQLException {
        int tipoVisitaId = rs.getInt("visita_tipo_visita_id");

        var tipoVisita = (TipoVisitaPreview) cache.get(tipoVisitaId);
        if (tipoVisita == null) {
            tipoVisita = tipoVisitaPreviewMapper.map(rs);
            cache.put(tipoVisitaId, tipoVisita);
        }

        return new Visita(
                rs.getInt("visita_id"),
                tipoVisita,
                rs.getDate("visita_data_svolgimento").toLocalDate(),
                coppiaIdUsernameMapper.map(rs),
                Visita.StatoVisita.valueOf(rs.getString("visita_stato"))
        );
    }
}
