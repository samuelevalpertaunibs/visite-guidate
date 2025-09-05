package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facade.IVisitaFacade;
import com.unibs.facade.VisitaFacade;
import com.unibs.models.Visita;
import com.unibs.services.ServiceFactory;
import com.unibs.views.ElencoVisiteView;
import com.unibs.views.components.PopupChiudi;
import com.unibs.views.components.PopupConferma;

import java.util.ArrayList;
import java.util.List;

public class VisitaController {
    private final WindowBasedTextGUI gui;
    private ElencoVisiteView elencoVisiteView;
    private final IVisitaFacade visitaFacade;

    public VisitaController(WindowBasedTextGUI gui, IVisitaFacade visitaFacade) {
        this.gui = gui;
        this.visitaFacade = visitaFacade;
    }

    public void apriVisualizzaVisitePerTipologia(List<Visita.StatoVisita> statiDaMostrare) {
        elencoVisiteView = new ElencoVisiteView("Visualizza elenco visite per stato", false);
        initElencoVisiteViewListener(statiDaMostrare);
        elencoVisiteView.mostra(gui);
    }

    private void initElencoVisiteViewListener(List<Visita.StatoVisita> stati) {
        try {
            elencoVisiteView.setStati(stati, stato -> {
                List<Visita> visite;
                if (stato != Visita.StatoVisita.EFFETTUATA) {
                    visite = visitaFacade.getVisitePreviewByStato(stato);
                } else {
                    visite = visitaFacade.getVisiteFromArchivio();
                }
                elencoVisiteView.aggiornaVisite(visite, stato);
            });
        } catch (Exception e) {
            elencoVisiteView.mostraErrore(e.getMessage());
        }
    }

    public Boolean apriCreazionePiano() {
        try {
            if (visitaFacade.isRaccoltaDisponibilitaChiusa()) {
                visitaFacade.rimuoviVecchieDisponibilita();
                visitaFacade.riapriRaccoltaDisponibilita();
                String messaggio = "Il piano delle visite è già stato creato ma il processo è\nstato interrotto prima della riapertura della raccolta disponibilità.\n\nLa riapertura delle dispobilità è appena stata eseguita con successo.";
                new PopupChiudi(gui).mostra("Attenzione!", messaggio);

                // Devo rimuovere il tasto perche la view non deve essere creata, ritorno null
                return null;
            }

            String messaggioConferma = "Verrà chiusa la raccolta disponibilità dei volontari e verrà\navviata la produzione del piano delle visite per il mese a venire.\nInoltre non sarà più possibile modificare le date precluse per il mese attuale.";
            if (new PopupConferma(gui).mostra("Attenzione!", messaggioConferma)) {
                visitaFacade.creaPiano();
                return true;
            }
        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
        // Ce stato un errore oppure il configuratore non ha confermato
        return false;
    }

    public void apriVisualizzaVisiteConIscrizione(String nomeFruitoreConIscrizione, List<Visita.StatoVisita> statiDaMostrare) {
        elencoVisiteView = new ElencoVisiteView("Visualizza elenco visite a cui sei iscritto", true);
        initElencoVisiteConIscrizioneViewListener(statiDaMostrare, nomeFruitoreConIscrizione);
        elencoVisiteView.mostra(gui);
    }

    private void initElencoVisiteConIscrizioneViewListener(List<Visita.StatoVisita> stati, String nomeFruitore) {
        try {
            elencoVisiteView.setStati(stati, stato -> {
                List<Visita> visite = visitaFacade.getVisitePreviewByFruitore(stato, nomeFruitore);
                List<List<String>> codiciPrenotazione = new ArrayList<>();
                for (Visita visita : visite) {
                    codiciPrenotazione.add(visitaFacade.getCodiciPrenotazioneFruitorePerVisita(nomeFruitore, visita.getId()));
                }
                elencoVisiteView.aggiornaVisite(visite, stato, codiciPrenotazione);
            });
        } catch (Exception e) {
            elencoVisiteView.mostraErrore(e.getMessage());
        }
    }
}
