package com.unibs.services;

import com.unibs.daos.DaoFactory;

public class ServiceFactory {
    private LuogoService luogoService;
    private ConfigService configService;
    private GiornoService giornoService;
    private VolontarioService volontarioService;
    private TipoVisitaService tipoVisitaService;
    private VisitaService visitaService;
    private DatePrecluseService datePrecluseService;
    private LoginService loginService;
    private FruitoreService fruitoreService;
    private final DaoFactory daoFactory = new DaoFactory();

    public LuogoService getLuogoService() {
        if (luogoService == null) {
            luogoService = new LuogoService();
        }
        return luogoService;
    }

    public LoginService getLoginService() {
        if (loginService == null) {
            loginService = new LoginService(daoFactory.getUtenteDao());
        }
        return loginService;
    }

    public ConfigService getConfigService() {
        if (configService == null) {
            configService = new ConfigService();
        }
        return configService;
    }

    public GiornoService getGiornoService() {
        if (giornoService == null) {
            giornoService = new GiornoService();
        }
        return giornoService;
    }

    public TipoVisitaService getTipoVisitaService() {
        if (tipoVisitaService == null) {
            tipoVisitaService = new TipoVisitaService(daoFactory.getTipoVisitaDao());
        }
        return tipoVisitaService;
    }

    public VisitaService getVisitaService() {
        if (visitaService == null) {
            visitaService = new VisitaService(getTipoVisitaService(), getConfigService(), getVolontarioService(), getGiornoService(), daoFactory.getVisitaDao());
        }
        return visitaService;
    }

    public DatePrecluseService getDatePrecluseService() {
        if (datePrecluseService == null) {
            datePrecluseService = new DatePrecluseService(getConfigService());
        }
        return datePrecluseService;
    }

    public VolontarioService getVolontarioService() {
        if (volontarioService == null) {
            if (tipoVisitaService == null) {
                this.tipoVisitaService = new TipoVisitaService(daoFactory.getTipoVisitaDao());
            }
            volontarioService = new VolontarioService(daoFactory.getUtenteDao(), tipoVisitaService, getGiornoService(), getDatePrecluseService());
        }
        return volontarioService;
    }

    public FruitoreService getFruitoreService() {
        if (fruitoreService == null) {
            fruitoreService = new FruitoreService(getConfigService(), getVisitaService(), daoFactory.getVisitaDao());
        }
        return fruitoreService;
    }
}