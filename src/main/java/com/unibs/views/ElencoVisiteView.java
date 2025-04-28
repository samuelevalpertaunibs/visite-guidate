package com.unibs.views;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.gui2.table.TableModel;
import com.unibs.models.Visita;

import java.util.List;
import java.util.function.Consumer;

public class ElencoVisiteView {

    private final Window window;
    private final ActionListBox statoList;
    private final Table<String> visiteTable;
    private final Label errorLabel;
    private final Panel statoPanel;
    private final Panel visitePanel;

    public ElencoVisiteView() {
        window = new BasicWindow("Visualizza elenco visite per stato");
        statoList = new ActionListBox();
        visiteTable = new Table<>("Titolo", "Descrizione", "Punto di incontro", "Data di svolgimento", "Ora inizio", "Entrata libera");
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

        visitePanel.addComponent(visiteTable);

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

    public void aggiornaVisiteTable(List<Visita> visite, Visita.StatoVisita stato) {
        if (stato == Visita.StatoVisita.CANCELLATA) {
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