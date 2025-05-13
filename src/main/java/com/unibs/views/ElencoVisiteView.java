package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.models.TipoVisita;
import com.unibs.models.Visita;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ElencoVisiteView {

    private final Window window;
    private final ActionListBox statoList;
    private final Label visitaLabel;
    private final Label errorLabel;
    private final Panel statoPanel;
    private final Panel visitePanel;
    private final Label counterLabel = new Label("");
    private final Button nextButton = new Button("Successivo");
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private List<Visita> visite;
    private Visita.StatoVisita statoAttuale;

    public ElencoVisiteView(String titolo) {
        window = new BasicWindow(titolo);
        counterLabel.setText("Nessuna visita caricata.");
        statoList = new ActionListBox();
        visitaLabel = new Label("");
        errorLabel = new Label("");
        statoPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        visitePanel = new Panel(new LinearLayout(Direction.VERTICAL));
        creaFinestra();
    }

    private void creaFinestra() {
        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        statoPanel.addComponent(statoList);
        statoPanel.addComponent(new EmptySpace());
        statoPanel.addComponent(new Button("Chiudi", window::close));

        visitePanel.addComponent(visitaLabel);
        visitePanel.addComponent(new EmptySpace());
        visitePanel.addComponent(counterLabel);

        nextButton.setEnabled(false);  // inizialmente disabilitato
        nextButton.addListener(button -> mostraProssimaVisita());
        visitePanel.addComponent(nextButton);

        mainPanel.addComponent(statoPanel.withBorder(Borders.singleLine("Filtra")));
        mainPanel.addComponent(visitePanel.withBorder(Borders.singleLine("Visite")));

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(mainPanel);
    }

    public void setStati(List<Visita.StatoVisita> stati, Consumer<Visita.StatoVisita> onStatoSelected) {
        statoList.clearItems();
        for (Visita.StatoVisita stato : stati) {
            statoList.addItem(stato.name(), () -> onStatoSelected.accept(stato));
        }
        statoList.runSelectedItem();
    }

    public void aggiornaVisite(List<Visita> visitePassate, Visita.StatoVisita stato) {
        this.visite = visitePassate;
        this.statoAttuale = stato;

        currentIndex.set(0);

        if (visite == null || visite.isEmpty()) {
            visitaLabel.setText("");
            visitaLabel.setText("Nessuna visita disponibile.");
            counterLabel.setVisible(false);
            nextButton.setEnabled(false);
            nextButton.setVisible(false);
        } else {
            counterLabel.setVisible(true);
            nextButton.setEnabled(true);
            nextButton.setVisible(true);
            aggiornaVisita(0);
        }
    }

    private void mostraProssimaVisita() {
        int next = currentIndex.incrementAndGet();
        if (next >= visite.size()) {
            currentIndex.set(0);
            next = 0;
        }
        aggiornaVisita(next);
    }

    private void aggiornaVisita(int index) {
        StringBuilder sb = new StringBuilder();
        Visita v = visite.get(index);
        TipoVisita tipoVisita = v.getTipoVisita();
        if (statoAttuale == Visita.StatoVisita.CANCELLATA) {
            sb.append("Titolo: ").append(tipoVisita.titolo());
            sb.append("\nLuogo: ").append(tipoVisita.luogo().getNome());
            sb.append("\nData di mancato svolgimento: ").append(v.getDataSvolgimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            sb.append("\nVolontario: ").append(v.getVolontario().getUsername());
            sb.append("\nStato: ").append(statoAttuale);
        } else {
            sb.append("Titolo: ").append(tipoVisita.titolo());
            sb.append("\nLuogo: ").append(tipoVisita.luogo().getNome());
            sb.append("\nDescrizione: ").append(tipoVisita.descrizione());
            sb.append("\nPunto di incontro: ").append(tipoVisita.puntoIncontro());
            sb.append("\nData di svolgimento: ").append(v.getDataSvolgimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            sb.append("\nOra d'inzio: ").append(tipoVisita.oraInizio());
            sb.append("\nEntrata libera: ").append(tipoVisita.entrataLibera() ? "Sì" : "No");
            sb.append("\nVolontario: ").append(v.getVolontario().getUsername());
            sb.append("\nStato: ").append(statoAttuale);
        }

        visitaLabel.setText(sb.toString());
        counterLabel.setText("Visita " + (index + 1) + " di " + visite.size());
    }


    public void mostra(WindowBasedTextGUI gui) {
        statoList.takeFocus();
        gui.addWindowAndWait(window);
    }

    public void mostraErrore(String errore) {
        visitePanel.addComponent(errorLabel);
        errorLabel.setText(errore);
    }
}