package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.models.*;
import com.unibs.services.*;
import com.unibs.utils.DatabaseException;
import com.unibs.views.*;
import com.unibs.views.components.PopupConferma;

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
    }

    private void initListenerAggiungiTipoVisitaView() {
        aggiungiTipoVisitaView.getFineButton().addListener(this::chiudiAggiungiTipoVisita);
        aggiungiTipoVisitaView.getSelezionaLuogoButton().addListener(this::apriSelezioneLuogo);
        aggiungiTipoVisitaView.getAssociaGiorniButton().addListener(this::apriSelezionaGiorni);
        aggiungiTipoVisitaView.getAssociaVolontariButton().addListener(this::apriSelezionaVolontari);
        aggiungiTipoVisitaView.getAggiungiButton().addListener(this::aggiungiTipoVisitaPoiAggiornaFinestra);
    }

    private void aggiungiTipoVisitaPoiAggiornaFinestra(Button button) {
        try {
            aggiuntiTipoVisita();

            // Aggiorno il pannello del recap dei TipoVisita aggiunti
            StringBuilder sb = new StringBuilder();
            List<String> tipiVisita = tipoVisitaService.getPreviewTipiVisita();
            for (String tipo : tipiVisita) {
                sb.append(" - ").append(tipo).append("\n");
            }

            aggiungiTipoVisitaView.aggiornaVisite(sb.toString());
            //luogoSelezionato = null;
            giorniSelezionati = new HashSet<>();
            volontariSelezionati = new HashSet<>();
        } catch (Exception e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaGiorni(Button button) {
        try {
            List<Giorno> giorni = giornoService.getGiorni();
            SelezioneMultiplaView<Giorno> selezioneMultiplaGiorni = new SelezioneMultiplaView<>(giorni);
            Set<Giorno> setSelezionati = new HashSet<>(giorniSelezionati);
            giorniSelezionati = selezioneMultiplaGiorni.mostra(gui, setSelezionati, "Seleziona i giorni della settimana in cui verra svolta la visita");
            aggiungiTipoVisitaView.aggiornaGiorniSelezionati(giorniSelezionati);
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaVolontari(Button button) {
        try {
            List<Volontario> volontari = volontarioService.findAllVolontari();
            SelezioneMultiplaView<Volontario> selezioneMultiplaVolontari = new SelezioneMultiplaView<>(volontari);
            Set<Volontario> setSelezionati = new HashSet<>(volontariSelezionati);
            volontariSelezionati = selezioneMultiplaVolontari.mostra(gui, setSelezionati, "Seleziona i volontari associabili alla visita");
            aggiungiTipoVisitaView.aggiornaVolontariSelezionati(volontariSelezionati);
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void chiudiAggiungiTipoVisita(Button button) {
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
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }

        // Aggiorno la view principale dopo aver selezionato un luogo
        aggiungiTipoVisitaView.aggiornaLuogo(luogoSelezionato.getNome());
        button.setEnabled(false);
        aggiungiTipoVisitaView.focusTitolo();
    }

    private void initListenerSelezionaLuogoView() {
        selezionaLuogoView.getAggiungiLuogoButton().addListener(this::apriAggiungiLuogo);
    }

    private void apriAggiungiLuogo(Button button) {
        aggiungiLuogoView = new AggiungiLuogoView();
        initListenerAggiungiLuogoInizializzazioneView();
        aggiungiLuogoView.mostra(gui);
    }

    private void initListenerAggiungiLuogoInizializzazioneView() {
        aggiungiLuogoView.getAggiungiLuogoButton().addListener(this::aggiungiLuogo);
        aggiungiLuogoView.getSelezionaComuneButton().addListener(this::apriSelezionaComune);
    }

    private void aggiungiLuogo(Button button) {
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
        SelezionaComuneView selezionaComuneView = new SelezionaComuneView();
        try {
            List<Comune> comuni = configService.getAmbitoTerritoriale();
            comuneSelezionato = selezionaComuneView.mostra(gui, comuni);
            button.setLabel(comuneSelezionato.getNome());
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

    public void apriInserisciNuovoLuogo() {
        aggiungiLuogoView = new AggiungiLuogoView();
        initListenerAggiungiLuogoView();
        aggiungiLuogoView.mostra(gui);
    }

    private void initListenerAggiungiLuogoView() {
        aggiungiLuogoView.getAggiungiLuogoButton().addListener(this::aggiungiNuovoLuogo);
        aggiungiLuogoView.getSelezionaComuneButton().addListener(this::apriSelezionaComune);
    }

    private void aggiungiNuovoLuogo(Button button) {
        String nome = aggiungiLuogoView.getNome();
        String descrizione = aggiungiLuogoView.getDescrizione();
        try {
            // Se il luogo non esiste lo salvo e mostro la finestra delle visite da associare
            luogoSelezionato = luogoService.aggiungiLuogo(nome, descrizione, comuneSelezionato);
            aggiungiLuogoView.chiudi();

            apriAggiungiTipiVisitaLuogoFissato();

        } catch (Exception e) {
            aggiungiLuogoView.mostraErrore(e.getMessage());
        }
    }

    private void apriAggiungiTipiVisitaLuogoFissato() {
        aggiungiTipoVisitaView = new AggiungiTipoVisitaView();
        initListenerAggiungiTipoVisitaLuogoFissatoView();
        // Fisso il luogo
        aggiungiTipoVisitaView.aggiornaLuogo(luogoSelezionato.getNome());
        aggiungiTipoVisitaView.getSelezionaLuogoButton().setEnabled(false);
        aggiungiTipoVisitaView.focusTitolo();
        aggiungiTipoVisitaView.mostra(gui);
    }

    private void initListenerAggiungiTipoVisitaLuogoFissatoView() {
        aggiungiTipoVisitaView.getFineButton().addListener(this::chiudiAggiungiTipoVisitaLuogoFissato);
        aggiungiTipoVisitaView.getAssociaGiorniButton().addListener(this::apriSelezionaGiorni);
        aggiungiTipoVisitaView.getAssociaVolontariButton().addListener(this::apriSelezionaVolontari);
        aggiungiTipoVisitaView.getAggiungiButton().addListener(this::aggiungiTipoVisitaLuogoFissato);
    }

    private void aggiungiTipoVisitaLuogoFissato(Button button) {
        try {
            aggiuntiTipoVisita();
            // Reimposto il luogo fissato
            aggiungiTipoVisitaView.aggiornaLuogo(luogoSelezionato.getNome());
            aggiungiTipoVisitaView.getSelezionaLuogoButton().setEnabled(false);

            // Aggiorno il pannello del recap dei TipoVisita aggiunti
            StringBuilder sb = new StringBuilder();
            List<String> tipiVisita = tipoVisitaService.getPreviewTipiVisita(luogoSelezionato.getNome());
            for (String tipo : tipiVisita) {
                sb.append(" - ").append(tipo).append("\n");
            }

            aggiungiTipoVisitaView.aggiornaVisite(sb.toString());
            //luogoSelezionato = null;
            giorniSelezionati = new HashSet<>();
            volontariSelezionati = new HashSet<>();
        } catch (Exception e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void aggiuntiTipoVisita() {
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

    private void chiudiAggiungiTipoVisitaLuogoFissato(Button button) {
        if (new PopupConferma(gui).mostra("Attenzione", "I dati non salvati andranno persi.\nSei sicuro di voler uscire?")) {
            aggiungiTipoVisitaView.chiudi();
        }
    }
}
