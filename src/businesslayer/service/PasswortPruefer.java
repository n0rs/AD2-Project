// Prüft Passwörter auf Sicherheit
package businesslayer.service;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class PasswortPruefer implements Pruefer {

    // Prüft, ob das Passwort sicher genug ist
    public static boolean pruefe(String password) {
        if (password.length() < 8) return false; // Mindestens 8 Zeichen
        // Prüft, ob das Passwort Buchstaben, Zahlen und Sonderzeichen enthält
        boolean hasLetter = password.matches(".*[a-zA-Z]+.*");
        boolean hasDigit = password.matches(".*\\d+.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>]+.*");
        return hasLetter && hasDigit && hasSpecial;
    }

    // Fragt den Nutzer so lange nach einem Passwort, bis ein sicheres Passwort eingegeben wurde
    public static String startePasswortPruefung(Scanner scanner) throws RemoteException, NotBoundException, MalformedURLException{
        String passwort;
        while(true) {
            Presenter.printMessage("Passwort: ");
            passwort = scanner.nextLine();
            if (pruefe(passwort)) {
                Presenter.printMessage("Starkes Passwort");
                break;
            } else {
                Presenter.printMessage("Schlechtes Passwort. Bitte versuchen Sie es erneut.");
            }
        }
        return passwort;
    }
}