package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class InserisciDisponibilitaView {

    private final CheckBoxList<WrappedDate> listaDate;
    private final Button salvaButton;
    private BasicWindow window;
    public InserisciDisponibilitaView() {
        listaDate = new CheckBoxList<>();
        salvaButton = new Button("Salva");
    }

    public void mostra(WindowBasedTextGUI gui) {
        this.window = creaFinestra();
        gui.addWindowAndWait(window);
    }

    private BasicWindow creaFinestra() {
        window = new BasicWindow("Inserisci Disponibilità");

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label("Seleziona le tue disponibilità:"));
        panel.addComponent(listaDate);

        panel.addComponent(salvaButton);

        Button annullaButton = new Button("Chiudi", window::close);
        panel.addComponent(annullaButton);

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        return window;
    }

    public void setDateDisponibili(List<LocalDate> dateDisponibili) {
        listaDate.clearItems();
        for (LocalDate data : dateDisponibili) {
            listaDate.addItem(new WrappedDate(data));
        }
    }

    public List<LocalDate> getDateSelezionate() {
        return listaDate.getCheckedItems().stream().map(WrappedDate::date).collect(Collectors.toList());
    }

    public void setDateSelezionate(List<LocalDate> dateSelezionate) {
        for (int i = 0; i < listaDate.getItemCount(); i++) {
            WrappedDate item = listaDate.getItemAt(i);
            boolean shouldCheck = dateSelezionate.contains(item.getDate());
            listaDate.setChecked(listaDate.getItemAt(i), shouldCheck);
        }
    }

    public Button getSalvaButton() {
        return salvaButton;
    }

    private record WrappedDate(LocalDate date) {

        @Override
        public String toString() {
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public LocalDate getDate() {
            return date;
        }
    }

}