package com.unibs;

import java.io.PrintStream;
import java.util.Scanner;

public class View {

    private final Scanner scanner;
    private final PrintStream printStream;

    public View() {
        this.scanner = new Scanner(System.in);
        this.printStream = new PrintStream(System.out);
    }

    public void showMessage(String message) {
        printStream.println(message);
    }

    public String getInput(String prompt) {
        printStream.print(prompt);
        return scanner.nextLine().trim();
    }

    public String getLimitedInput(String message, int maxLength) {
        printStream.print(message);
        String input = scanner.nextLine().trim();
        return input.length() > maxLength ? input.substring(0, maxLength) : input;
    }

    public void clearScreen() {
        printStream.print("\033[H\033[2J");
        printStream.flush();
    }

    public void clearScreen(String message) {
        clearScreen();
        showMessage(message + '\n');
        getInput("Premi Invio per continuare...");
    }

    public void showTitle(String title) {
        showMessage("======== " + title + " ========");
    }

    public void clearScreenAndShowTitle(String title) {
        clearScreen();
        showTitle(title);
    }

    public boolean getConfirm() {
        return getYesOrNo("Confermi la tua scelta?");
    }

    public boolean getConfirmWithDefaultNo() {
        return getYesOrNoWithDefaultNo("Confermi la tua scelta?");
    }

    public boolean getYesOrNo(String message) {
        do {
            String input = getInput(message + " [Y/n] ");
            if (input.isEmpty() || input.equalsIgnoreCase("Y")) {
                return true;
            }

            if (input.equalsIgnoreCase("N")) {
                return false;
            }

        } while (true);
    }

    public boolean getYesOrNoWithDefaultNo(String message) {
        do {
            String input = getInput(message + " [y/N] ");
            if (input.isEmpty() || input.equalsIgnoreCase("N")) {
                return false;
            }

            if (input.equalsIgnoreCase("Y")) {
                return true;
            }

        } while (true);
    }

    public int getInt(String title, String message, int min) {
        while (true) {
            clearScreenAndShowTitle(title);
            String input = getInput(message);

            try {
                int value = Integer.parseInt(input);
                if (value >= min) {
                    return value; // Ritorna il numero se valido
                } else {
                    clearScreen("Errore: il numero deve essere almeno " + min + ".");
                }
            } catch (NumberFormatException e) {
                clearScreen("Errore: inserisci un numero intero valido.");
            }
        }
    }
}
