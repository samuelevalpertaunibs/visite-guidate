package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facades.TipoVisitaFacade;
import com.unibs.models.*;
import com.unibs.services.ServiceFactory;
import com.unibs.utils.DatabaseException;
import com.unibs.views.*;
import com.unibs.views.components.PopupChiudi;
import com.unibs.views.components.PopupConferma;

import java.util.*;
import java.util.stream.Collectors;

public class TipoVisitaController {
    private final TipoVisitaFacade tvFacade;
    private final WindowBasedTextGUI gui;

    private AggiungiTipoVisitaView aggiungiTipoVisitaView;
    private SelezionaLuogoView selezionaLuogoView;
    private AggiungiLuogoView aggiungiLuogoView;

    private Luogo luogoSelezionato;
    private Comune comuneSelezionato;
    private Set<Giorno> giorniSelezionati;
    private Set<Volontario> volontariSelezionati;
    private SelezioneMultiplaView<Volontario> selezioneMultiplaVolontariView;

    public TipoVisitaController(WindowBasedTextGUI gui, ServiceFactory serviceFactory) {
        this.gui = gui;
        this.tvFacade = new TipoVisitaFacade(serviceFactory);
        this.aggiungiTipoVisitaView = new AggiungiTipoVisitaView();
        this.giorniSelezionati = new HashSet<>();
        this.volontariSelezionati = new HashSet<>();
    }

    public void apriAggiungiTipoVisita() {
        aggiungiTipoVisitaView = new AggiungiTipoVisitaView();
        initListenerAggiungiTipoVisitaView();
        aggiungiTipoVisitaView.mostra(gui);
        // Durante l'aggiunta di un tv il configuratore può creare dei volontari ma non è detto scelga di associarli, nel caso non succede li rimuovo
        tvFacade.rimuoviVolontariNonAssociati();
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
            aggiungiTipoVisita();
            // Reset valori selezionati
            luogoSelezionato = null;
            giorniSelezionati = new HashSet<>();
            volontariSelezionati = new HashSet<>();

            // Aggiorno il pannello del recap dei TipoVisita aggiunti
            StringBuilder sb = new StringBuilder();
            List<java.lang.String> tipiVisita = tvFacade.getNomiTipiVisita();
            for (java.lang.String tipo : tipiVisita) {
                sb.append(" - ").append(tipo).append("\n");
            }
            aggiungiTipoVisitaView.aggiornaVisite(sb.toString());
        } catch (Exception e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaGiorni(Button button) {
        try {
            List<Giorno> giorni = tvFacade.getGiorni();
            SelezioneMultiplaView<Giorno> selezioneMultiplaGiorni = new SelezioneMultiplaView<>(giorni, false);
            Set<Giorno> setSelezionati = new HashSet<>(giorniSelezionati);
            giorniSelezionati = selezioneMultiplaGiorni.mostra(gui, setSelezionati, "Seleziona i giorni della settimana in cui verra svolta la visita");
            aggiungiTipoVisitaView.aggiornaGiorniSelezionati(giorniSelezionati);
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaVolontari(Button button) {
        try {
            List<Volontario> volontari = tvFacade.cercaTuttiVolontari();
            selezioneMultiplaVolontariView = new SelezioneMultiplaView<>(volontari, true);
            Button aggiungiButton = selezioneMultiplaVolontariView.getAggiungiButton();
            if (aggiungiButton != null) {
                aggiungiButton.addListener(this::apriAggiungiVolontario);
            }
            Set<Volontario> setSelezionati = new HashSet<>(volontariSelezionati);
            volontariSelezionati = selezioneMultiplaVolontariView.mostra(gui, setSelezionati, "Seleziona i volontari da associare al tipo di visita");
            aggiungiTipoVisitaView.aggiornaVolontariSelezionati(volontariSelezionati);
        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriAggiungiVolontario(Button button) {
        AggiungiVolontarioView aggiungiVolontarioView = new AggiungiVolontarioView();
        aggiungiVolontarioView.getAggiungiButton().addListener((b) -> {
            String username = aggiungiVolontarioView.getUsername();
            try {
                tvFacade.aggiungiVolontario(username);
                aggiungiVolontarioView.chiudi();
                // Se il volontario è stato creato correttamente ricreo la finestra della selezione volontari con il nuovo volontario
                selezioneMultiplaVolontariView.chiudi();
                apriSelezionaVolontari(null);

            } catch (Exception e) {
                aggiungiVolontarioView.mostraErrore(e.getMessage());
            }
        });
        aggiungiVolontarioView.mostra(gui);

    }

    private void chiudiAggiungiTipoVisita(Button button) {
        try {
            if (tvFacade.esisteAlmenoUnaVisita()) {
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
            List<Luogo> luoghi = tvFacade.cercaTuttiLuoghi();
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
            Luogo luogoAggiunto = tvFacade.aggiungiLuogo(nome, descrizione, comuneSelezionato);
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
            List<Comune> comuni = tvFacade.getAmbitoTerritoriale();
            comuneSelezionato = selezionaComuneView.mostra(gui, comuni, "Seleziona un comune");
            button.setLabel(comuneSelezionato.nome());
            aggiungiLuogoView.focusAggiungiLuogoButton();
        } catch (DatabaseException e) {
            aggiungiLuogoView.mostraErrore(e.getMessage());
        }
    }

    public void apriVisualizzaVisitePerVolontari() {
        ElencoVolontariView elencoVolontariView = new ElencoVolontariView();
        List<Volontario> volontari = tvFacade.cercaTuttiVolontari();
        for (Volontario v : volontari) {
            List<java.lang.String> titoliTipiVisitaAssociati = tvFacade.cercaNomiDeiTipiVisitaDelVolontario(v.getId());
            elencoVolontariView.aggiungiVolontario(v, titoliTipiVisitaAssociati);
        }
        elencoVolontariView.mostra(gui);
    }

    public void apriVisualizzaVisitePerLuoghi() {
        ElencoLuoghiConVisiteAssociateView elencoLuoghiConVisiteAssociateView = new ElencoLuoghiConVisiteAssociateView();
        List<Luogo> luogi = tvFacade.cercaTuttiLuoghi();
        for (Luogo l : luogi) {
            List<java.lang.String> titoliTipiVisitaAssociati = tvFacade.cercaTipiVisitaNelLuogo(l.getId());
            elencoLuoghiConVisiteAssociateView.aggiungiLuogo(l, titoliTipiVisitaAssociati);
        }
        elencoLuoghiConVisiteAssociateView.mostra(gui);
    }

    public void apriVisualizzaTipiVisiteAssociateAlVolontario(Volontario volontario) {
        TVAssociateAlVolontarioView view = new TVAssociateAlVolontarioView(volontario);
        List<TipoVisita> visite = tvFacade.cercaVisiteDelVolontario(volontario.getId());
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
            luogoSelezionato = tvFacade.aggiungiLuogo(nome, descrizione, comuneSelezionato);
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
            aggiungiTipoVisita();

            // Reimposto il luogo fissato
            aggiungiTipoVisitaView.aggiornaLuogo(luogoSelezionato.getNome());
            aggiungiTipoVisitaView.getSelezionaLuogoButton().setEnabled(false);

            // Reset valori selezionati
            giorniSelezionati = new HashSet<>();
            volontariSelezionati = new HashSet<>();

            // Aggiorno il pannello del recap dei TipoVisita aggiunti
            StringBuilder sb = new StringBuilder();
            List<java.lang.String> tipiVisita = tvFacade.getNomiTipiVisita(luogoSelezionato.getNome());
            for (java.lang.String tipo : tipiVisita) {
                sb.append(" - ").append(tipo).append("\n");
            }
            aggiungiTipoVisitaView.aggiornaVisite(sb.toString());

        } catch (Exception e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void aggiungiTipoVisita() throws Exception {
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
        tvFacade.aggiungiTipoVisita(titolo, descrizione, dataInizio, dataFine, oraInizio, durata, entrataLibera, numeroMin, numeroMax, luogoSelezionato, volontariSelezionati, giorniSelezionati, indirizzo, comune, provincia);
        aggiungiTipoVisitaView.clearAll();
    }

    private void chiudiAggiungiTipoVisitaLuogoFissato(Button button) {
        if (new PopupConferma(gui).mostra("Attenzione", "I dati non salvati andranno persi.\nSei sicuro di voler uscire?")) {
            aggiungiTipoVisitaView.chiudi();
        }
    }

    public void apriRimuoviTipoVisita() {
        SelezionaTipoVisitaView view = new SelezionaTipoVisitaView("Rimuovi tipo visita");
        view.setTitoliTipiVisita(tvFacade.cercaTuttiTipiVisita());
        view.setOnTipoVisitaSelected(titolo -> {
            try {
                tvFacade.inserisciTipoVisitaDaRimuovere(titolo);
                new PopupChiudi(gui).mostra("", "Il tipo di visita verrà rimosso con successo");
            } catch (Exception e) {
                new PopupChiudi(gui).mostra("Errore", e.getMessage());
            } finally {
                view.chiudi();
            }
        });
        view.mostra(gui);
    }

    public void apriRimuoviVolontario() {
        RimuoviVolontarioView view = new RimuoviVolontarioView();
        List<java.lang.String> nomi = tvFacade.cercaTuttiVolontari().stream().map(Volontario::getUsername).collect(Collectors.toList());
        view.setVolontari(nomi);
        view.setOnVolontarioSelected(nome -> {
            try {
                tvFacade.inserisciVolontarioDaRimuovere(nome);
                new PopupChiudi(gui).mostra("", "Il volontario verrà rimosso con successo");
            } catch (Exception e) {
                new PopupChiudi(gui).mostra("Errore", e.getMessage());
            } finally {
                view.chiudi();
            }
        });
        view.mostra(gui);
    }

    public void apriInserisciNuovoTipoVisita() {
        selezionaLuogoView = new SelezionaLuogoView();
        selezionaLuogoView.getAggiungiLuogoButton().setVisible(false);
        try {
            List<Luogo> luoghi = tvFacade.cercaTuttiLuoghi();
            luogoSelezionato = selezionaLuogoView.mostra(gui, luoghi);
            // Dopo che l'utente ha selezionato il luogo mostro la view per creare delle visite
            aggiungiTipoVisitaView = new AggiungiTipoVisitaView();
            initListenerAggiungiTipoVisitaLuogoFissatoView();
            // Fisso il luogo
            aggiungiTipoVisitaView.aggiornaLuogo(luogoSelezionato.getNome());
            aggiungiTipoVisitaView.getSelezionaLuogoButton().setEnabled(false);
            aggiungiTipoVisitaView.focusTitolo();
            aggiungiTipoVisitaView.mostra(gui);
        } catch (DatabaseException e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }


    }

    public void associaNuoviVolontari() {
        SelezionaTipoVisitaView view = new SelezionaTipoVisitaView("Seleziona tipo visita a cui associare nuovi volontari");
        view.setTitoliTipiVisita(tvFacade.cercaTuttiTipiVisita());
        view.setOnTipoVisitaSelected(titolo -> {
            try {
                apriAggiungiVolontari(titolo);
            } catch (Exception e) {
                new PopupChiudi(gui).mostra("Errore", e.getMessage());
            } finally {
                view.chiudi();
            }
        });
        view.mostra(gui);
    }

    private void apriAggiungiVolontari(java.lang.String nomeTipoVisita) {
        try {
            Optional<Integer> tipoVisitaIdOptional = tvFacade.cercaIDTipoVisitaPerNome(nomeTipoVisita);
            if (tipoVisitaIdOptional.isEmpty()) {
                throw new DatabaseException("Tipo visita non trovato");
            }
            int tipoVisitaId = tipoVisitaIdOptional.get();
            HashMap<Integer, String> volontariAssociabili = tvFacade.cercaVolontariAssociabiliAlTipoVisita(tipoVisitaId);
            if (volontariAssociabili.isEmpty()) {
                throw new IllegalArgumentException("Tutti i volontari esistenti sono già associati a questo tipo di visita");
            }
            SelezioneMultiplaView<Volontario> selezioneMultiplaVolontari = new SelezioneMultiplaView<>(volontariAssociabili.stream().toList(), false);
            volontariSelezionati = selezioneMultiplaVolontari.mostra(gui, new HashSet<>(), "Seleziona i volontari da associare al tipo di visita");
            if (volontariSelezionati.isEmpty()) {
                new PopupChiudi(gui).mostra("", "Nessun volontario aggiunto.");
                return;
            }
            tvFacade.associaVolontariAlTipoVisita(volontariSelezionati, tipoVisitaId);
            new PopupChiudi(gui).mostra("", "I volontari sono stati associati correttamente.");
        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
    }
}
