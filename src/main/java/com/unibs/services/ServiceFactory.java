package com.unibs.services;

public class ServiceFactory {
    private LuogoService luogoService;
    private ConfigService configService;
    private GiornoService giornoService;
    private VolontarioService volontarioService;
    private TipoVisitaService tipoVisitaService;
    private VisitaService visitaService;
    private DatePrecluseService datePrecluseService;
    private LoginService loginService;

    public LuogoService getLuogoService() {
        if (luogoService == null) {
            luogoService = new LuogoService();
        }
        return luogoService;
    }

    public LoginService getLoginService() {
        if (loginService == null) {
            loginService = new LoginService();
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
            tipoVisitaService = new TipoVisitaService(getLuogoService(), getGiornoService());
            if (volontarioService != null) {
                tipoVisitaService.setVolontarioService(volontarioService);
            }
        }
        return tipoVisitaService;
    }

    public VisitaService getVisitaService() {
        if (visitaService == null) {
            visitaService = new VisitaService(getTipoVisitaService(), getConfigService(), getVolontarioService(), getGiornoService());
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
                this.tipoVisitaService = new TipoVisitaService(getLuogoService(), getGiornoService());
            }
            volontarioService = new VolontarioService(tipoVisitaService, getGiornoService(), getDatePrecluseService());
            tipoVisitaService.setVolontarioService(volontarioService);
        }
        return volontarioService;
    }
}