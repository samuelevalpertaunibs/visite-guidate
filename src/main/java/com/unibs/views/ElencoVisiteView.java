package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.unibs.controllers.VisitaController;
import com.unibs.models.Visita;

import java.util.List;

public class ElencoVisiteView {
    private final VisitaController visitaController;

    public ElencoVisiteView(VisitaController visitaController) {
        this.visitaController = visitaController;
    }

    public Window creaFinestra() {
        Window window = new BasicWindow("Visualizza elenco visite per stato");

        Panel mainPanel = new Panel(new LinearLayout(Direction.HORIZONTAL));

        // === Sinistra: Lista stati visita ===
        Panel statoPanel = new Panel(new LinearLayout(Direction.VERTICAL));
        statoPanel.addComponent(new Label("Stato visita:"));

        ActionListBox statoList = new ActionListBox();

        // === Destra: Tabella visite ===
        Panel visitePanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Table<String> visiteTable = new Table<>("ID", "Tipo", "Data");


        // Aggiungi tutti gli stati alla lista selezionabile
        for (Visita.StatoVisita stato : Visita.StatoVisita.values()) {
            final Visita.StatoVisita statoFinal = stato;

            statoList.addItem(stato.name(), () -> {
                visiteTable.getTableModel().clear();

                List<Visita> visite = visitaController.getVisiteByStato(statoFinal);
                for (Visita v : visite) {
                    visiteTable.getTableModel().addRow(
                            String.valueOf(v.getId()),
                            v.getTipoVisita().getNome(),
                            v.getDataSvolgimento().toString()
                    );
                }
            });
        }

        statoPanel.addComponent(statoList);
        statoPanel.addComponent(new EmptySpace());
        statoPanel.addComponent(new Button("Chiudi", window::close));

        // === Aggiunta pannelli ===
        mainPanel.addComponent(statoPanel.withBorder(Borders.singleLine("Stati")));
        visitePanel.addComponent(visiteTable);
        mainPanel.addComponent(visitePanel.withBorder(Borders.singleLine("Visite")));

        window.setComponent(mainPanel);

        return window;
    }
}
