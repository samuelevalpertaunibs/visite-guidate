package com.unibs.daos;

import com.unibs.mappers.MapperFactory;

public class DaoFactory {

    private final MapperFactory mapperFactory;

    private ComuneDao comuneDao;
    private ConfigDao configDao;
    private DatePrecluseDao datePrecluseDao;
    private GiorniDao giorniDao;
    private LuogoDao luogoDao;
    private TipoVisitaDao tipoVisitaDao;
    private TipoVisitaPreviewDao tipoVisitaPreviewDao;
    private TipoVisitaCoreDao tipoVisitaCoreDao;
    private UtenteDao utenteDao;
    private VisitaDao visitaDao;

    public DaoFactory() {
        this.mapperFactory = new MapperFactory();
    }

    public ComuneDao getComuneDao() {
        if (comuneDao == null) {
            comuneDao = new ComuneDao(mapperFactory.getComuneMapper());
        }
        return comuneDao;
    }

    public ConfigDao getConfigDao() {
        if (configDao == null) {
            configDao = new ConfigDao(mapperFactory.getConfigMapper());
        }
        return configDao;
    }

    public DatePrecluseDao getDatePrecluseDao() {
        if (datePrecluseDao == null) {
            datePrecluseDao = new DatePrecluseDao();
        }
        return datePrecluseDao;
    }

    public GiorniDao getGiorniDao() {
        if (giorniDao == null) {
            giorniDao = new GiorniDao();
        }
        return giorniDao;
    }

    public LuogoDao getLuogoDao() {
        if (luogoDao == null) {
            luogoDao = new LuogoDao(mapperFactory.getLuogoMapper());
        }
        return luogoDao;
    }

    public TipoVisitaDao getTipoVisitaDao() {
        if (tipoVisitaDao == null) {
            tipoVisitaDao = new TipoVisitaDao(mapperFactory.getTipoVisitaMapper(), getUtenteDao(), getGiorniDao());
        }
        return tipoVisitaDao;
    }

    public TipoVisitaPreviewDao getTipoVisitaPreviewDao() {
        if (tipoVisitaPreviewDao == null) {
            tipoVisitaPreviewDao = new TipoVisitaPreviewDao(mapperFactory.getTipoVisitaPreviewMapper());
        }
        return tipoVisitaPreviewDao;
    }

    public TipoVisitaCoreDao getTipoVisitaCoreDao() {
        if (tipoVisitaCoreDao == null) {
            tipoVisitaCoreDao = new TipoVisitaCoreDao(mapperFactory.getTipoVisitaCoreMapper());
        }
        return tipoVisitaCoreDao;
    }

    public UtenteDao getUtenteDao() {
        if (utenteDao == null) {
            utenteDao = new UtenteDao(mapperFactory.getUtenteMapper(), mapperFactory.getCoppiaIdUsernameMapper());
        }
        return utenteDao;
    }

    public VisitaDao getVisitaDao() {
        if (visitaDao == null) {
            visitaDao = new VisitaDao(mapperFactory.getVisitaMapper());
        }
        return visitaDao;
    }
}