// Implementiert Pruefer
package businesslayer.service;

import java.util.Scanner;

import presentationlayer.Presenter;

public class PasswortPruefer implements Pruefer {
    
    @Override
    public boolean pruefe(String password) {
        if (password.length() < 8) return false;
        // Nutzt regular expressions um zu prüfen ob das Passwort mindestens einen Buchstaben, eine Zahl und ein Sonderzeichen enthält
        // .* bedeutet beliebig viele Zeichen, [a-zA-Z] bedeutet ein Buchstabe, \\d bedeutet eine Zahl und [!@#$%^&*(),.?":{}|<>] bedeutet ein Sonderzeichen
        // + bedeutet mindestens einmal
        boolean hasLetter = password.matches(".*[a-zA-Z]+.*");
        boolean hasDigit = password.matches(".*\\d+.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>]+.*");
        return hasLetter && hasDigit && hasSpecial;
    }

    public static String startePasswortPruefung(Scanner scanner){
        String passwort;
        while(true) {
            Presenter.printMessage("Wie lautet Ihr Passwort: ");
            passwort = scanner.nextLine();

            PasswortPruefer pruefer = new PasswortPruefer();
            if (pruefer.pruefe(passwort)) {
                Presenter.printMessage("Starkes Passwort");
                break;
            } else {
                Presenter.printMessage("Schlechtes Passwort. Bitte versuchen Sie es erneut.");
            }
        }
        return passwort;
    }
}