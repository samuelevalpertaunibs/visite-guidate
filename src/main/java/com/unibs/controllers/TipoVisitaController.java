package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.unibs.facades.ITipoVisitaFacade;
import com.unibs.models.*;
import com.unibs.models.adapters.AdapterElementoSelezionabile;
import com.unibs.utils.DatabaseException;
import com.unibs.views.*;
import com.unibs.views.components.PopupChiudi;
import com.unibs.views.components.PopupConferma;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TipoVisitaController {
    private final ITipoVisitaFacade tvFacade;
    private final WindowBasedTextGUI gui;

    private AggiungiTipoVisitaView aggiungiTipoVisitaView;
    private SelezionaLuogoView selezionaLuogoView;
    private AggiungiLuogoView aggiungiLuogoView;

    private Luogo luogoSelezionato;
    private Comune comuneSelezionato;
    private Set<Giorno> giorniSelezionati;
    private Set<CoppiaIdUsername> volontariSelezionati;
    private SelezioneMultiplaView<AdapterElementoSelezionabile<CoppiaIdUsername>> selezioneMultiplaVolontariView;

    public TipoVisitaController(WindowBasedTextGUI gui, ITipoVisitaFacade tvFacade) {
        this.gui = gui;
        this.tvFacade = tvFacade;
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

            // Crea adapter con lambda che definisce come mostrare il placeholder
            List<AdapterElementoSelezionabile<Giorno>> giorniSelezionabili =
                    giorni.stream()
                            .map(g -> new AdapterElementoSelezionabile<>(g, Giorno::getNome))
                            .toList();

            SelezioneMultiplaView<AdapterElementoSelezionabile<Giorno>> selezioneMultiplaGiorni =
                    new SelezioneMultiplaView<>(giorniSelezionabili, false);

            // Mappa i giorni già selezionati agli adapter
            Set<AdapterElementoSelezionabile<Giorno>> preSelezionatiAdapter =
                    giorniSelezionabili.stream()
                            .filter(a -> giorniSelezionati.contains(a.getValue()))
                            .collect(Collectors.toSet());

            // Mostra la view e ottieni gli adapter selezionati
            Set<AdapterElementoSelezionabile<Giorno>> selezionatiAdapter =
                    selezioneMultiplaGiorni.mostra(gui, preSelezionatiAdapter, "Seleziona i giorni della settimana");

            // Estrai i valori reali dai rispettivi adapter
            giorniSelezionati = selezionatiAdapter.stream()
                    .map(AdapterElementoSelezionabile::getValue)
                    .collect(Collectors.toSet());

            aggiungiTipoVisitaView.aggiornaGiorniSelezionati(giorniSelezionati);

        } catch (DatabaseException e) {
            aggiungiTipoVisitaView.mostraErrore(e.getMessage());
        }
    }

    private void apriSelezionaVolontari(Button button) {
        try {
            // Prendi tutti i volontari dal facade
            Set<CoppiaIdUsername> volontari = tvFacade.cercaTuttiVolontari();

            // Crea gli adapter per la view
            List<AdapterElementoSelezionabile<CoppiaIdUsername>> volontariSelezionabili =
                    volontari.stream()
                            .map(v -> new AdapterElementoSelezionabile<>(v, CoppiaIdUsername::getUsername))
                            .toList();

            selezioneMultiplaVolontariView = new SelezioneMultiplaView<>(volontariSelezionabili, true);

            // Aggiungi listener per il pulsante "Nuovo" se presente
            Optional.ofNullable(selezioneMultiplaVolontariView.getAggiungiButton())
                    .ifPresent(b -> b.addListener(this::apriAggiungiVolontario));

            // Mappa i volontari già selezionati agli adapter corrispondenti
            Set<AdapterElementoSelezionabile<CoppiaIdUsername>> preSelezionatiAdapter =
                    volontariSelezionabili.stream()
                            .filter(a -> volontariSelezionati.contains(a.getValue()))
                            .collect(Collectors.toSet());

            // Mostra la view e ottieni gli adapter selezionati
            Set<AdapterElementoSelezionabile<CoppiaIdUsername>> selezionatiAdapter =
                    selezioneMultiplaVolontariView.mostra(gui, preSelezionatiAdapter,
                            "Seleziona i volontari da associare al tipo di visita");

            // Estrai i valori reali dai rispettivi adapter
            volontariSelezionati = selezionatiAdapter.stream()
                    .map(AdapterElementoSelezionabile::getValue)
                    .collect(Collectors.toSet());

            // Aggiorna la view principale con i volontari selezionati
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
        Set<CoppiaIdUsername> volontari = tvFacade.cercaTuttiVolontari();
        for (CoppiaIdUsername v : volontari) {
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
        List<java.lang.String> nomi = tvFacade.cercaTuttiVolontari().stream().map(CoppiaIdUsername::getUsername).collect(Collectors.toList());
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

    private void apriAggiungiVolontari(String nomeTipoVisita) {
        try {
            // Ottieni l'id del tipo visita
            int tipoVisitaId = tvFacade.cercaIDTipoVisitaPerNome(nomeTipoVisita)
                    .orElseThrow(() -> new DatabaseException("Tipo visita non trovato"));

            // Ottieni i volontari associabili
            Set<CoppiaIdUsername> volontariAssociabili = tvFacade.cercaVolontariAssociabiliAlTipoVisita(tipoVisitaId);
            if (volontariAssociabili.isEmpty()) {
                new PopupChiudi(gui).mostra("", "Tutti i volontari esistenti sono già associati a questo tipo di visita.");
                return;
            }

            // Crea adapter per la selezione multipla
            List<AdapterElementoSelezionabile<CoppiaIdUsername>> volontariSelezionabili =
                    volontariAssociabili.stream()
                            .map(v -> new AdapterElementoSelezionabile<>(v, CoppiaIdUsername::getUsername))
                            .toList();

            // Mostra la view
            SelezioneMultiplaView<AdapterElementoSelezionabile<CoppiaIdUsername>> selezioneMultiplaVolontari =
                    new SelezioneMultiplaView<>(volontariSelezionabili, false);

            Set<AdapterElementoSelezionabile<CoppiaIdUsername>> preSelezionati = Set.of(); // nessuno pre-selezionato
            Set<AdapterElementoSelezionabile<CoppiaIdUsername>> selezionatiAdapter =
                    selezioneMultiplaVolontari.mostra(gui, preSelezionati, "Seleziona i volontari da associare al tipo di visita");

            if (selezionatiAdapter.isEmpty()) {
                new PopupChiudi(gui).mostra("", "Nessun volontario aggiunto.");
                return;
            }

            // Estrai i volontari reali dai rispettivi adapter
            Set<CoppiaIdUsername> selezionati = selezionatiAdapter.stream()
                    .map(AdapterElementoSelezionabile::getValue)
                    .collect(Collectors.toSet());

            // Associa al tipo visita
            tvFacade.associaVolontariAlTipoVisita(selezionati, tipoVisitaId);

            new PopupChiudi(gui).mostra("", "I volontari sono stati associati correttamente.");

        } catch (Exception e) {
            new PopupChiudi(gui).mostra("Errore", e.getMessage());
        }
    }
}
