package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.Visita;
import com.unibs.services.ConfigService;
import com.unibs.services.VisitaService;
import com.unibs.views.ElencoVisiteView;
import com.unibs.views.components.PopupChiudi;
import com.unibs.views.components.PopupConferma;

import java.util.List;

public class VisitaController {
    private final WindowBasedTextGUI gui;
    private final VisitaService visitaService;
    private final ConfigService configService;
    private ElencoVisiteView elencoVisiteView;

    public VisitaController(WindowBasedTextGUI gui, VisitaService visitaService, ConfigService configService) {
        this.gui = gui;
        this.visitaService = visitaService;
        this.configService = configService;
    }

    public void apriVisualizzaVisitePerTipologia(List<Visita.StatoVisita> statiDaMostrare) {
        elencoVisiteView = new ElencoVisiteView("Visualizza elenco visite per stato");
        initElencoVisiteViewListener(statiDaMostrare);
        elencoVisiteView.mostra(gui);
    }

    private void initElencoVisiteViewListener(List<Visita.StatoVisita> stati) {
        try {
            elencoVisiteView.setStati(stati, stato -> {
                List<Visita> visite;
                if (stato != Visita.StatoVisita.EFFETTUATA) {
                    visite = visitaService.getVisitePreviewByStato(stato);
                } else {
                    visite = visitaService.getVisiteFromArchivio();
                }
                elencoVisiteView.aggiornaVisite(visite, stato);
            });
        } catch (Exception e) {
            elencoVisiteView.mostraErrore(e.getMessage());
        }
    }

    public Boolean apriCreazionePiano() {
        try {
            if (configService.isRaccoltaDisponibilitaChiusa()) {
                visitaService.rimuoviVecchieDisponibilita();
                configService.riapriRaccoltaDisponibilita();
                String messaggio = "Il piano delle visite è già stato creato ma il processo è\nstato interrotto prima della riapertura della raccolta disponibilità.\n\nLa riapertura delle dispobilità è appena stata eseguita con successo.";
                new PopupChiudi(gui).mostra("Attenzione!", messaggio);

                // Devo rimuovere il tasto perche la view non deve essere creata, ritorno null
                return null;
            }

            String messaggioConferma = "Verrà chiusa la raccolta disponibilità dei volontari e verrà\navviata la produzione del piano delle visite per il mese a venire.\nInoltre non sarà più possibile modificare le date precluse per il mese attuale.";
            if (new PopupConferma(gui).mostra("Attenzione!", messaggioConferma)) {
                visitaService.creaPiano();
                return true;
            }
        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
        // Ce stato un errore oppure il configuratore non ha confermato
        return false;
    }

}
