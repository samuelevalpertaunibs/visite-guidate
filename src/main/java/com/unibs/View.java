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

    public View(Scanner scanner, PrintStream printStream) {
        this.scanner = scanner;
        this.printStream = printStream;
    }

    public void showMessage(String message) {
        printStream.println(message);
    }

    public String getInput(String prompt) {
        printStream.print(prompt);
        return scanner.nextLine();
    }


    public String getLimitedInput(String message, int maxLength) {
        printStream.print(message);
        String input = scanner.nextLine();
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

    public boolean confirmDefaultY(String message) {
        do {
            String input = getInput(message + " [Y/n] ");
            if (input.isEmpty() || input.equalsIgnoreCase("Y")) {
                return true;
            }

            if (input.equalsIgnoreCase("N")) {
                return false;
            }
            showMessage("Input non valido. Rispondi con 'Y' per confermare o 'N' per annullare.");
        } while (true);
    }
    public boolean confirmDefaultN(String message) {
        do {
            String input = getInput(message + " [y/N] ");
            if (input.isEmpty() || input.equalsIgnoreCase("N")) {
                return false;
            }

            if (input.equalsIgnoreCase("Y")) {
                return true;
            }
            showMessage("Input non valido. Rispondi con 'Y' per confermare o 'N' per annullare.");
        } while (true);
    }

    public int getInt(String title, String message, int min) {
        while (true) {
            clearScreen();
            showTitle(title);
            showMessage(message);
            String input = scanner.nextLine().trim();

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
