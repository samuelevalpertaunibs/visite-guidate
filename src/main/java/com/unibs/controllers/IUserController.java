package com.unibs.controllers;

import com.googlecode.lanterna.gui2.Window;

public interface IUserController {
    void start();
    void showMenu();

    Window getView();
}
