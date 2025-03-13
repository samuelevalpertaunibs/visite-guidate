package com.unibs;

public class App {
    public static void main(String[] args) {
        View view = new View();
        LoginController loginController = new LoginController(view);
        loginController.start();
    }

}
