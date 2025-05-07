package com.unibs.views;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class InputNumericoView {
    private final String titolo;
    private final String richiestaInput;

    private final AtomicReference<Integer> valoreInserito = new AtomicReference<>(null);
    private Label erroreLabel;
    private Window window;
    private TextBox inputBox;

    public InputNumericoView(String titolo, String richiestaInput) {
        this.titolo = titolo;
        this.richiestaInput = richiestaInput;
    }

    public Integer mostra(WindowBasedTextGUI gui) {
        creaFinestra();
        gui.addWindowAndWait(window);
        return valoreInserito.get();
    }

    private void creaFinestra() {
        window = new BasicWindow(titolo);
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label(richiestaInput));
        inputBox = new TextBox();
        panel.addComponent(inputBox);

        erroreLabel = new Label("").setForegroundColor(TextColor.ANSI.RED);
        panel.addComponent(erroreLabel);

        Panel pulsanti = getPulsanti();
        panel.addComponent(pulsanti);

        window.setComponent(panel);
        window.setHints(List.of(Window.Hint.MENU_POPUP, Window.Hint.CENTERED, Window.Hint.EXPANDED));
    }

    private Panel getPulsanti() {
        Button conferma = new Button("Conferma", () -> {
            try {
                int valore = Integer.parseInt(inputBox.getText().trim());
                valoreInserito.set(valore);
                window.close();
            } catch (NumberFormatException e) {
                erroreLabel.setText("Inserisci un numero valido.");
            }
        });

        Button chiudi = new Button("Chiudi", window::close);

        Panel pulsanti = new Panel(new LinearLayout(Direction.VERTICAL));
        pulsanti.addComponent(conferma);
        pulsanti.addComponent(chiudi);
        return pulsanti;
    }
}