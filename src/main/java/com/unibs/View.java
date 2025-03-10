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

    public int getIntInput(String prompt, int min, int max) {
        while (true) {
            String input = getInput(prompt);
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    showMessage("Inserisci un numero tra " + min + " e " + max);
                }
            } catch (NumberFormatException e) {
                showMessage("Input non valido. Inserisci un numero.");
            }
        }
    }
}