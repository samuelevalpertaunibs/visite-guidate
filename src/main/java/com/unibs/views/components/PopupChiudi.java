package com.unibs.views.components;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

public class PopupChiudi {

    private final WindowBasedTextGUI gui;

    public PopupChiudi(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    public void mostra(String titolo, String messaggio) {
        new MessageDialogBuilder()
                .setTitle(titolo)
                .setText(messaggio)
                .addButton(MessageDialogButton.CLOSE)
                .build()
                .showDialog(gui);
    }
}