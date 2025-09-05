package com.unibs.mappers;

import com.unibs.models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class TipoVisitaMapper {
    private final TipoVisitaCoreMapper coreMapper;
    private final LuogoMapper luogoMapper;

    public TipoVisitaMapper(TipoVisitaCoreMapper coreMapper, LuogoMapper luogoMapper) {
        this.coreMapper = coreMapper;
        this.luogoMapper = luogoMapper;
    }

    public TipoVisita map(ResultSet rs, Set<CoppiaIdUsername> volontari, Set<Giorno> giorni) throws SQLException {
        TipoVisitaCore core = coreMapper.map(rs);
        Luogo luogo = luogoMapper.map(rs);

        return new TipoVisita(core, luogo, giorni, volontari);
    }

    public TipoVisita map(Map<String, Object> dataMap, Set<CoppiaIdUsername> volontari, Set<Giorno> giorni) {

        TipoVisitaCore core = coreMapper.map(dataMap);
        Luogo luogo = luogoMapper.map(dataMap);

        // Costruzione finale del TipoVisita completo
        return new TipoVisita(core, luogo, giorni, volontari);
    }
}