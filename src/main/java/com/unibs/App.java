package com.unibs;

public class App {
    public static void main(String[] args) {
        LoginService loginService = new LoginService();
        View view = new View();
        LoginController loginController = new LoginController(loginService, view);
        loginController.start();
    }

}
