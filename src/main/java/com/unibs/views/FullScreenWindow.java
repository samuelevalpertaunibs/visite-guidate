package com.unibs.views;

import com.googlecode.lanterna.gui2.*;

import java.util.Arrays;

public class FullScreenWindow extends BasicWindow {
    public FullScreenWindow(String title) {
        super(title);
        setHints(Arrays.asList(Window.Hint.FULL_SCREEN)); // Rendi la finestra full screen
    }
}
