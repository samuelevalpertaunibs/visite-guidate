package com.unibs.controllers;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;

public interface IUserController {
    void start();
    void showMenu();
    void mostraAvvisoNonRegime(WindowBasedTextGUI gui);
}
