package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.unibs.controllers.VolontarioController;

import java.util.List;

public class InserisciDisponibilitaView {
    private final VolontarioController controller;

    public InserisciDisponibilitaView(VolontarioController controller) {
        this.controller = controller;
    }

//    public void mostra(WindowBasedTextGUI gui, int volontarioId) {
//        BasicWindow finestra = creaFinestra(gui, volontarioId);
//        gui.addWindowAndWait(finestra);
//    }

//    public BasicWindow creaFinestra(WindowBasedTextGUI gui, int volontarioId) {
//        BasicWindow window = new BasicWindow("Inserisci Disponibilità");
//
//        // Creazione del layout della finestra
//        Panel panel = new Panel();
//        panel.setLayoutManager(new GridLayout(2));
//
//        // Recupera le date disponibili per il volontario
//        List<String> dateDisponibili = controller.getDateDisponibiliPerVolontario(volontarioId);
//
//        // Crea una lista di checkbox per ogni data disponibile
//        MultiSelectListBox<String> listaDate = new MultiSelectListBox<>();
//        for (String data : dateDisponibili) {
//            listaDate.addItem(data);
//        }
//
//        panel.addComponent(new Label("Seleziona le tue disponibilità:"));
//        panel.addComponent(listaDate);
//
//        // Aggiungi il bottone "Salva"
//        Button salvaButton = new Button("Salva", () -> {
//            List<String> selezionate = listaDate.getSelectedItems();
//            // Salva le disponibilità selezionate
//            controller.salvaDisponibilita(volontarioId, selezionate);
//            window.close();
//        });
//        panel.addComponent(salvaButton);
//
//        // Aggiungi il bottone "Annulla"
//        Button annullaButton = new Button("Annulla", () -> window.close());
//        panel.addComponent(annullaButton);
//
//        window.setComponent(panel);
//        return window;
//    }
}
