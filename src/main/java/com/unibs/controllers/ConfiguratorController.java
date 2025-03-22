package com.unibs.controllers;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.unibs.services.ConfigService;
import com.unibs.services.ConfiguratorService;
import com.unibs.services.TipoVisitaService;
import com.unibs.models.User;
import com.unibs.views.ConfiguratorView;

public class ConfiguratorController implements IUserController {

    private final ConfiguratorService configuratorService;
    private final ConfiguratorView view;
    private final User user;
    private final ConfigService configService;
    private final MultiWindowTextGUI gui;
    private final TipoVisitaService tipoVisitaService;

    public ConfiguratorController(MultiWindowTextGUI gui, User currentUser) {
        this.configuratorService = new ConfiguratorService();
        this.configService = new ConfigService();
        this.tipoVisitaService = new TipoVisitaService();
        this.view = new ConfiguratorView(this);
        this.user = currentUser;
        this.gui = gui;
    }

    @Override
    public void start() {

        // Se le configurazioni non sono ancora state inizializzate
     /*   if (!configService.isInitialized()) {
            configService.initDefault();
            ConfigController configController = new ConfigController(this.gui);
            gui.addWindowAndWait(configController.getView());
            // Config inizializzate, configurazione dati
            TipoVisitaController tipoVisitaController = new TipoVisitaController(this.gui);
            tipoVisitaController.apriAggiungiTipoVisita();
        }*/
        DatePrecluseController controller = new DatePrecluseController(gui);
        controller.apriAggiungiDatePrecluse();

        showMenu();
    }

    @Override
    public void showMenu() {
        throw new UnsupportedOperationException("TODO: showMenu");
    }

    @Override
    public Window getView() {
        return this.view.creaFinestra();
    }

}
