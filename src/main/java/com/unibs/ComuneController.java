package com.unibs;

import com.unibs.models.Comune;

import java.util.ArrayList;

public class ComuneController {
    private final ComuneService comuneService;
    private final View view;

    protected ComuneController(View view) {
        this.comuneService = new ComuneService();
        this.view = view;
    }

    public Comune selezionaComune() {
        try {
            ArrayList<Comune> comuni = comuneService.getAllComuni();

            String[] comuni_string = new String[comuni.size()];
            for (int i = 0; i < comuni.size(); i++) {
                comuni_string[i] = comuni.get(i).toString();
            }
            int scelta = view.showMenu("Seleziona un comune: ", comuni_string);

            return comuni.get(scelta);

        } catch (DatabaseException e) {
            view.showMessage("Errore durante il recupero dei comuni: " + e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            view.showMessage("Errore imprevisto: " + e.getMessage());
            System.exit(-1);
        }

        return null;
    }

}
