package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
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
        Panel statoPanel = new Panel(new LinearLayout(Direction.VERTICAL));

        ActionListBox statoList = new ActionListBox();

        Panel visitePanel = new Panel(new LinearLayout(Direction.VERTICAL));
        Table<String> visiteTable = new Table<>("Titolo", "Descrizione", "Punto di incontro", "Data di svolgimento", "Ora inizio", "Entrata libera");

        // Aggiungi tutti gli stati alla lista selezionabile
        for (Visita.StatoVisita stato : Visita.StatoVisita.values()) {
            final Visita.StatoVisita statoFinal = stato;

            statoList.addItem(stato.name(), () -> {
                List<Visita> visite = visitaController.getVisitePreview(statoFinal);

                if (statoFinal == Visita.StatoVisita.CANCELLATA) {
                    visiteTable.setTableModel(new TableModel<>("Titolo", "Data di (mancato) svolgimento"));
                    for (Visita v : visite) {
                        visiteTable.getTableModel().addRow(
                            v.getTipoVisita().getTitolo(),
                                v.getDataSvolgimento().toString()
                        );
                    }
                } else {
                    visiteTable.setTableModel(new TableModel<>("Titolo", "Descrizione", "Punto di incontro", "Data di svolgimento", "Ora inizio", "Entrata libera"));
                    for (Visita v : visite) {
                        visiteTable.getTableModel().addRow(
                                v.getTipoVisita().getTitolo(),
                                v.getTipoVisita().getDescrizione(),
                                v.getTipoVisita().getPuntoIncontro().toString(),
                                v.getDataSvolgimento().toString(),
                                v.getTipoVisita().getOraInizio().toString(),
                                v.getTipoVisita().isEntrataLibera() ? "Si" : "No"
                        );
                    }
                }

            });
        }

        statoPanel.addComponent(statoList);
        statoList.runSelectedItem();

        statoPanel.addComponent(new EmptySpace());
        statoPanel.addComponent(new Button("Chiudi", window::close));

        mainPanel.addComponent(statoPanel.withBorder(Borders.singleLine("Filtra")));
        visitePanel.addComponent(visiteTable);
        mainPanel.addComponent(visitePanel.withBorder(Borders.singleLine("Visite")));

        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        window.setComponent(mainPanel);

        return window;
    }
}
