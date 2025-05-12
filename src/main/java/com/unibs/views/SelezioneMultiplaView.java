package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.utils.ElementoSelezionabile;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SelezioneMultiplaView<T extends ElementoSelezionabile> {
    // elementi lo tengo come List perche un Set perde l'ordine, e invece potrebbe essere rilevante nella view
    private final List<T> elementi;
    private Window window;
    private final Map<T, CheckBox> checkBoxMap = new HashMap<>();
    private final Button aggiungiButton;

    public SelezioneMultiplaView(List<T> elementi, boolean creaAggiungiButton) {
        this.elementi = elementi;
        if (creaAggiungiButton) {
            aggiungiButton = new Button("Nuovo");
        } else {
            aggiungiButton = null;
        }
    }

    public Button getAggiungiButton() {
        return aggiungiButton;
    }

    private Window creaFinestra(AtomicReference<Set<T>> selezionatiRef, Set<T> preSelezionati, String titolo) {
        window = new BasicWindow(titolo);
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        // Creo le checkbox per ogni elemento
        for (T elemento : elementi) {
            CheckBox checkBox = new CheckBox(elemento.getPlaceHolder());
            if (preSelezionati.contains(elemento)) {
                checkBox.setChecked(true);
            }
            checkBoxMap.put(elemento, checkBox);
            panel.addComponent(checkBox);
        }

        if (aggiungiButton != null) {
            panel.addComponent(aggiungiButton);
            panel.addComponent(new EmptySpace());
        }

        // Bottone conferma
        panel.addComponent(new Button("Conferma", () -> {
            Set<T> selezionati = new HashSet<>();
            for (Map.Entry<T, CheckBox> entry : checkBoxMap.entrySet()) {
                if (entry.getValue().isChecked()) {
                    selezionati.add(entry.getKey());
                }
            }
            selezionatiRef.set(selezionati);
            window.close();
        }));

        window.setHints(List.of(Window.Hint.CENTERED, Window.Hint.MODAL));
        window.setComponent(panel);
        return window;
    }

    public void chiudi() {
        window.close();
    }

    public Set<T> mostra(WindowBasedTextGUI gui, Set<T> preSelezionati, String titolo) {
        AtomicReference<Set<T>> selezionatiReference = new AtomicReference<>(Set.of());
        window = creaFinestra(selezionatiReference, preSelezionati, titolo);
        gui.addWindowAndWait(window);
        return selezionatiReference.get();
    }
}