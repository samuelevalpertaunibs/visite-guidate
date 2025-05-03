package com.unibs.views.components;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

public class PopupConferma {
    private final WindowBasedTextGUI gui;

    public PopupConferma(WindowBasedTextGUI gui) {
        this.gui = gui;
    }

    public boolean mostra(String titolo, String messaggio) {
        MessageDialogButton result = new MessageDialogBuilder()
                .setTitle(titolo)
                .setText(messaggio)
                .addButton(MessageDialogButton.CONTINUE)
                .addButton(MessageDialogButton.ABORT)
                .build()
                .showDialog(gui);

        return result == MessageDialogButton.CONTINUE;
    }
}
