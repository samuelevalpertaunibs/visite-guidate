package com.unibs;

import java.io.IOException;
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
        try{
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                //il comando viene lanciato solo se eseguiamo l'app da cmd.exe
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                printStream.print("\033[H\033[2J");
                printStream.flush();
            }
        }catch(IOException | InterruptedException e){
            showMessage(e.getMessage());
        }
    }

    public void clearScreen(String message) {
        clearScreen();
        showMessage(message + '\n');
        getInput("Premi Invio per continuare...");
    }

    public void showTitle(String title) {
        showMessage("======== " + title + " ========");
    }

    public boolean getConfirm() {
        do {
            String input = getInput("Confermi la tua scelta? [Y/n] ");
            if (input.isEmpty() || input.equalsIgnoreCase("Y")) {
                return true;
            }

            if (input.equalsIgnoreCase("N")) {
                return false;
            }

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
