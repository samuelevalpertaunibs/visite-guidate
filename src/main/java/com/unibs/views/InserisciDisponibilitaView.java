package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.time.LocalDate;
import java.util.List;

public class InserisciDisponibilitaView {

    private BasicWindow window;
    private final CheckBoxList<LocalDate> listaDate;
    private final Button salvaButton;
    private Label erroreLabel;

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

        erroreLabel = new Label("");
        panel.addComponent(erroreLabel);

        panel.addComponent(salvaButton);
        panel.addComponent(new Label("Attenzione: premendo <Salva> verranno sovrascritte le scelte precedenti."));

        Button annullaButton = new Button("Annulla", window::close);
        panel.addComponent(annullaButton);

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
        return window;
    }

    public void setDateDisponibili(List<LocalDate> dateDisponibili) {
        listaDate.clearItems();
        for (LocalDate data : dateDisponibili) {
            listaDate.addItem(data);
        }
    }

    public List<LocalDate> getDateSelezionate() {
        return listaDate.getCheckedItems();
    }

    public Button getSalvaButton() {
        return salvaButton;
    }

    public void chiudi() {
        window.close();
    }

    public void mostraErrore(String message) {
        erroreLabel.setText(message);
    }
}