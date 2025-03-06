package com.unibs;


public class App {
    public static void main(String[] args) {
        UserService userService = new UserService();
        View view = new View();
        MainController mainController = new MainController(userService, view);
        mainController.start();
    }

}
