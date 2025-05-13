package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.*;
import com.unibs.services.*;
import com.unibs.utils.DatabaseException;
import com.unibs.views.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TipoVisitaController {
    private final TipoVisitaService tipoVisitaService;
    private final LuogoService luogoService;
    private final WindowBasedTextGUI gui;
    private final ConfigService configService;
    private final GiornoService giornoService;
    private final VolontarioService volontarioService;
    private AggiungiTipoVisitaView aggiungiTipoVisitaView;
    private SelezionaLuogoView selezionaLuogoView;
    private AggiungiLuogoView aggiungiLuogoView;
    private Luogo luogoSelezionato;
    private Comune comuneSelezionato;
    private Set<Giorno> giorniSelezionati;
    private Set<Volontario> volontariSelezionati;
    private SelezioneMultiplaView<Volontario> selezioneMultiplaVolontariView;

    protected TipoVisitaController(WindowBasedTextGUI gui, LuogoService luogoService, ConfigService configService, GiornoService giornoService, VolontarioService volontarioService, TipoVisitaService tipoVisitaService) {
        this.gui = gui;
        this.luogoService = luogoService;
        this.configService = configService;
        this.giornoService = giornoService;
        this.volontarioService = volontarioService;
        this.tipoVisitaService = tipoVisitaService;
        this.aggiungiTipoVisitaView = new AggiungiTipoVisitaView();
        this.giorniSelezionati = new HashSet<>();
        this.volontariSelezionati = new HashSet<>();
    }

    public void apriAggiungiTipoVisita() {
        aggiungiTipoVisitaView = new AggiungiTipoVisitaView();
        initListenerAggiungiTipoVisitaView();
        aggiungiTipoVisitaView.mostra(gui);
        // Durante l'aggiunta di un tv il configuratore può creare dei volontari ma non è detto scelga di associarli, nel caso non succede li rimuovo
        volontarioService.rimuoviNonAssociati();
    }

    private void initListenerAggiungiTipoVisitaView() {
        aggiungiTipoVisitaView.getFineButton().addListener(button3 -> chiudiAggiungiTipoVisita());
        aggiungiTipoVisitaView.getSelezionaLuogoButton().addListener(this::apriSelezioneLuogo);
        aggiungiTipoVisitaView.getAssociaGiorniButton().addListener(button2 -> apriSelezionaGiorni());
        aggiungiTipoVisitaView.getAssociaVolontariButton().addListener(button1 -> apriSelezionaVolontari());
        aggiungiTipoVisitaView.getAggiungiButton().addListener(button -> aggiungiTipoVisitaPoiAggiornaFinestra());
    }

    private void aggiungiTipoVisitaPoiAggiornaFinestra() {
        try {
            aggiungiTipoVisita();
            // Reset valori selezionati
            luogoSelezionato = null;
            giorniSelezionati = new HashSet<>();
            volontariSelezionati = new HashSet<>();

            // Aggiorno il pannello del recap dei TipoVisita aggiunti
            StringBuilder sb = new StringBuilder();
            List<String> tipiVisita = tipoVisitaService.getPreviewTipiVisita();
            for (String tipo : tipiVisita) {
                sb.append(" - ").append(tipo).append("\n");
            }
            aggiungiTipoVisitaView.aggiornaVisite(sb.toString());
        } catch (Exception e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaGiorni() {
        try {
            List<Giorno> giorni = giornoService.getGiorni();
            SelezioneMultiplaView<Giorno> selezioneMultiplaGiorni = new SelezioneMultiplaView<>(giorni, false);
            Set<Giorno> setSelezionati = new HashSet<>(giorniSelezionati);
            giorniSelezionati = selezioneMultiplaGiorni.mostra(gui, setSelezionati, "Seleziona i giorni della settimana in cui verra svolta la visita");
            aggiungiTipoVisitaView.aggiornaGiorniSelezionati(giorniSelezionati);
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaVolontari() {
        try {
            List<Volontario> volontari = volontarioService.findAllVolontari();
            selezioneMultiplaVolontariView = new SelezioneMultiplaView<>(volontari, true);
            Button aggiungiButton = selezioneMultiplaVolontariView.getAggiungiButton();
            if (aggiungiButton != null) {
                aggiungiButton.addListener(button1 -> apriAggiungiVolontario());
            }
            Set<Volontario> setSelezionati = new HashSet<>(volontariSelezionati);
            volontariSelezionati = selezioneMultiplaVolontariView.mostra(gui, setSelezionati, "Seleziona i volontari da associare al tipo di visita");
            aggiungiTipoVisitaView.aggiornaVolontariSelezionati(volontariSelezionati);
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriAggiungiVolontario() {
        AggiungiVolontarioView aggiungiVolontarioView = new AggiungiVolontarioView();
        aggiungiVolontarioView.getAggiungiButton().addListener((b) -> {
            String username = aggiungiVolontarioView.getUsername();
            try {
                volontarioService.aggiungiVolontario(username);
                aggiungiVolontarioView.chiudi();
                // Se il volontario è stato creato correttamente ricreo la finestra della selezione volontari con il nuovo volontario
                selezioneMultiplaVolontariView.chiudi();
                apriSelezionaVolontari();

            } catch (Exception e) {
                aggiungiVolontarioView.mostraErrore(e.getMessage());
            }
        });
        aggiungiVolontarioView.mostra(gui);

    }

    private void chiudiAggiungiTipoVisita() {
        try {
            if (tipoVisitaService.esisteAlmenoUnaVisita()) {
                aggiungiTipoVisitaView.chiudi();
            } else {
                aggiungiTipoVisitaView.mostraErrore("Inserisci almeno un tipo di visita");
            }
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezioneLuogo(Button button) {
        selezionaLuogoView = new SelezionaLuogoView();
        initListenerSelezionaLuogoView();
        try {
            List<Luogo> luoghi = luogoService.findAll();
            luogoSelezionato = selezionaLuogoView.mostra(gui, luoghi);
            // Aggiorno la view principale dopo aver selezionato un luogo
            aggiungiTipoVisitaView.aggiornaLuogo(luogoSelezionato.getNome());
            button.setEnabled(false);
            aggiungiTipoVisitaView.focusTitolo();
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void initListenerSelezionaLuogoView() {
        selezionaLuogoView.getAggiungiLuogoButton().addListener(button -> apriAggiungiLuogo());
    }

    private void apriAggiungiLuogo() {
        aggiungiLuogoView = new AggiungiLuogoView();
        initListenerAggiungiLuogoInizializzazioneView();
        aggiungiLuogoView.mostra(gui);
    }

    private void initListenerAggiungiLuogoInizializzazioneView() {
        aggiungiLuogoView.getAggiungiLuogoButton().addListener(button -> aggiungiLuogo());
        aggiungiLuogoView.getSelezionaComuneButton().addListener(this::apriSelezionaComune);
    }

    private void aggiungiLuogo() {
        String nome = aggiungiLuogoView.getNome();
        String descrizione = aggiungiLuogoView.getDescrizione();
        try {
            // Imposto il luogo aggiunto nella SelezionaLuogoView, che viene letto quando serve dal controller
            Luogo luogoAggiunto = luogoService.aggiungiLuogo(nome, descrizione, comuneSelezionato);
            selezionaLuogoView.setLuogo(luogoAggiunto);
            aggiungiLuogoView.chiudi();
            selezionaLuogoView.chiudi();
        } catch (Exception e) {
            aggiungiLuogoView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaComune(Button button) {
        SelezionaElementoView<Comune> selezionaComuneView = new SelezionaElementoView<>();
        try {
            List<Comune> comuni = configService.getAmbitoTerritoriale();
            comuneSelezionato = selezionaComuneView.mostra(gui, comuni, "Seleziona un comune");
            button.setLabel(comuneSelezionato.nome());
            aggiungiLuogoView.focusAggiungiLuogoButton();
        } catch (DatabaseException e) {
            aggiungiLuogoView.mostraErrore(e.getMessage());
        }
    }

    public void apriVisualizzaVisitePerVolontari() {
        ElencoVolontariView elencoVolontariView = new ElencoVolontariView();
        List<Volontario> volontari = volontarioService.findAllVolontari();
        for (Volontario v : volontari) {
            List<String> titoliTipiVisitaAssociati = tipoVisitaService.getTitoliByVolontarioId(v.getId());
            elencoVolontariView.aggiungiVolontario(v, titoliTipiVisitaAssociati);
        }
        elencoVolontariView.mostra(gui);
    }

    public void apriVisualizzaVisitePerLuoghi() {
        ElencoLuoghiConVisiteAssociateView elencoLuoghiConVisiteAssociateView = new ElencoLuoghiConVisiteAssociateView();
        List<Luogo> luogi = luogoService.findAll();
        for (Luogo l : luogi) {
            List<String> titoliTipiVisitaAssociati = tipoVisitaService.getTitoliByLuogoId(l.getId());
            elencoLuoghiConVisiteAssociateView.aggiungiLuogo(l, titoliTipiVisitaAssociati);
        }
        elencoLuoghiConVisiteAssociateView.mostra(gui);
    }

    public void apriVisualizzaTipiVisiteAssociateAlVolontario(Volontario volontario) {
        TVAssociateAlVolontarioView view = new TVAssociateAlVolontarioView(volontario);
        List<TipoVisita> visite = tipoVisitaService.findByVolontario(volontario.getId());
        view.impostaVisite(visite);
        view.mostra(gui);
    }

    private void aggiungiTipoVisita() {
        String titolo = aggiungiTipoVisitaView.getTitolo();
        String descrizione = aggiungiTipoVisitaView.getDescrizione();
        String dataInizio = aggiungiTipoVisitaView.getDataInizio();
        String dataFine = aggiungiTipoVisitaView.getDataFine();
        String oraInizio = aggiungiTipoVisitaView.getOraInizio();
        String durata = aggiungiTipoVisitaView.getDurata();
        boolean entrataLibera = aggiungiTipoVisitaView.getEntrataLibera();
        String numeroMin = aggiungiTipoVisitaView.getNumeroMinPartecipanti();
        String numeroMax = aggiungiTipoVisitaView.getNumeroMaxPartecipanti();
        String indirizzo = aggiungiTipoVisitaView.getIndirizzo();
        String comune = aggiungiTipoVisitaView.getComune();
        String provincia = aggiungiTipoVisitaView.getProvincia();
        tipoVisitaService.aggiungiTipoVisita(titolo, descrizione, dataInizio, dataFine, oraInizio, durata, entrataLibera, numeroMin, numeroMax, luogoSelezionato, volontariSelezionati, giorniSelezionati, indirizzo, comune, provincia);
        aggiungiTipoVisitaView.clearAll();
    }

}
