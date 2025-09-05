package com.unibs.mappers;

import com.unibs.models.Giorno;
import com.unibs.models.Luogo;
import com.unibs.models.TipoVisita;
import com.unibs.models.TipoVisitaCore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

public class TipoVisitaMapper {
    private final TipoVisitaCoreMapper coreMapper;
    private final LuogoMapper luogoMapper;

    public TipoVisitaMapper(TipoVisitaCoreMapper coreMapper, LuogoMapper luogoMapper) {
        this.coreMapper = coreMapper;
        this.luogoMapper = luogoMapper;
    }

    public TipoVisita map(ResultSet rs, HashMap<Integer, String> volontari, Set<Giorno> giorni) throws SQLException {
        TipoVisitaCore core = coreMapper.map(rs);
        Luogo luogo = luogoMapper.map(rs);

        return new TipoVisita(core, luogo, giorni, volontari);
    }
}