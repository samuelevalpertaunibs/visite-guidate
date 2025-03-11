package com.unibs;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
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

    public void showMenu(String[] options) {
        clearScreen();
        showTitle("Menu Configuratore");

        for (int i = 0; i < options.length; i++) {
            showMessage((i + 1) + "\t" + options[i]);
        }

        showMessage("0\tEsci\n");

        int choice;
        do {
            String input = getInput("Digita il numero dell'opzione desiderata > ");
            try {
                choice = Integer.parseInt(input);
                if (choice < 0 || choice > options.length) {
                    showMessage("Errore: valore fuori dal range consentito. Riprova.\n");
                }
            } catch (NumberFormatException e) {
                choice = -1;
                showMessage("Errore: inserisci un numero valido.\n");
            }
        } while (choice < 0 || choice > options.length);

        showMessage("Hai scelto: " + (choice == 0 ? "Uscita" : options[choice - 1]));

    }
}
