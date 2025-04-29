package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.utils.SelezionabileConCheckbox;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SelezioneMultiplaView<T extends SelezionabileConCheckbox> {
    // elementi lo tengo come List perche un Set perde l'ordine, e invece potrebbe essere rilevante nella view
    private final List<T> elementi;
    private Window window;
    private final Map<T, CheckBox> checkBoxMap = new HashMap<>();

    public SelezioneMultiplaView(List<T> elementi) {
        this.elementi = elementi;
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

    public Set<T> mostra(WindowBasedTextGUI gui, Set<T> preSelezionati, String titolo) {
        AtomicReference<Set<T>> selezionatiReference = new AtomicReference<>(Set.of());
        window = creaFinestra(selezionatiReference, preSelezionati, titolo);
        gui.addWindowAndWait(window);
        return selezionatiReference.get();
    }
}