package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.models.TipoVisita;
import com.unibs.models.Volontario;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TVAssociateAlVolontarioView {
    private final Volontario volontario;
    private final Label visitaLabel = new Label("");
    private final Label counterLabel = new Label("");
    private final Button nextButton = new Button("Successivo");
    private final Panel panel = new Panel();
    private List<TipoVisita> tipiVisita;
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private Window window;

    public TVAssociateAlVolontarioView(Volontario volontario) {
        this.volontario = volontario;
        setupUI();
    }

    private void setupUI() {
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        counterLabel.setText("Nessuna visita caricata.");
        panel.addComponent(counterLabel);
        panel.addComponent(new EmptySpace());

        panel.addComponent(visitaLabel);
        panel.addComponent(new EmptySpace());

        nextButton.setEnabled(false);  // inizialmente disabilitato
        nextButton.addListener(button -> mostraProssimaVisita());
        panel.addComponent(nextButton);

        panel.addComponent(new Button("Chiudi", () -> window.close()));
    }

    public void impostaVisite(List<TipoVisita> tvPassati) {
        this.tipiVisita = tvPassati;
        currentIndex.set(0);

        if (tipiVisita == null || tipiVisita.isEmpty()) {
            visitaLabel.setText("");
            counterLabel.setText("Nessuna visita disponibile.");
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
            aggiornaVisita(0);
        }
    }

    private void mostraProssimaVisita() {
        int next = currentIndex.incrementAndGet();
        if (next >= tipiVisita.size()) {
            currentIndex.set(0);
            next = 0;
        }
        aggiornaVisita(next);
    }

    private void aggiornaVisita(int index) {
        TipoVisita visita = tipiVisita.get(index);
        visitaLabel.setText(visita.toString());
        counterLabel.setText("Visita " + (index + 1) + " di " + tipiVisita.size());
    }

    public void mostra(WindowBasedTextGUI gui) {
        window = new BasicWindow("Tipi di visita associati a " + volontario.getUsername());
        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(panel);
        gui.addWindowAndWait(window);
    }
}