package com.unibs.mappers;


public class MapperFactory {

    private ComuneMapper comuneMapper;
    private ConfigMapper configMapper;
    private LuogoMapper luogoMapper;
    private TipoVisitaCoreMapper coreMapper;
    private TipoVisitaPreviewMapper tvPreviewMapper;
    private TipoVisitaMapper tvMapper;
    private VisitaMapper visitaMapper;
    private UtenteMapper utenteMapper;

    public ComuneMapper getComuneMapper() {
        if (comuneMapper == null) {
            comuneMapper = new ComuneMapper();
        }
        return comuneMapper;
    }

    public ConfigMapper getConfigMapper() {
        if (configMapper == null) {
            configMapper = new ConfigMapper();
        }
        return configMapper;
    }

    public LuogoMapper getLuogoMapper() {
        if (luogoMapper == null) {
            luogoMapper = new LuogoMapper(getComuneMapper());
        }
        return luogoMapper;
    }

    public TipoVisitaCoreMapper getTipoVisitaCoreMapper() {
        if (coreMapper == null) {
            coreMapper = new TipoVisitaCoreMapper();
        }
        return coreMapper;
    }

    public TipoVisitaPreviewMapper getTipoVisitaPreviewMapper() {
        if (tvPreviewMapper == null) {
            tvPreviewMapper = new TipoVisitaPreviewMapper(getTipoVisitaCoreMapper());
        }
        return tvPreviewMapper;
    }

    public TipoVisitaMapper getTipoVisitaMapper() {
        if (tvMapper == null) {
            tvMapper = new TipoVisitaMapper(getTipoVisitaCoreMapper(), getLuogoMapper());
        }
        return tvMapper;
    }

    public VisitaMapper getVisitaMapper() {
        if (visitaMapper == null) {
            visitaMapper = new VisitaMapper(getTipoVisitaMapper(), getTipoVisitaPreviewMapper());
        }
        return visitaMapper;
    }

    public UtenteMapper getUtenteMapper() {
        if (utenteMapper == null) {
            utenteMapper = new UtenteMapper();
        }
        return utenteMapper;
    }
}