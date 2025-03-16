package com.unibs;

import com.unibs.models.Luogo;

public class TipoVisitaController {
        private final TipoVisitaService tipoVisitaService;
        private final View view;

        protected TipoVisitaController(View view) {
            this.tipoVisitaService = new TipoVisitaService();
            this.view = view;
        }

    public void aggiungiTipoVisita() {
        LuogoController luogoController = new LuogoController(view);

        do {
            view.clearScreenAndShowTitle("Aggiunta di un tipo di visita");
            Luogo luogo = luogoController.selezionaLuogo();
            throw new RuntimeException("TODO");

        } while (view.getYesOrNoWithDefaultNo("Vuoi inserire un'altro comune?"));
    }
}
