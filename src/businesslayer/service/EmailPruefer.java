// Prüft E-Mail-Adressen und deren Einzigartigkeit
package businesslayer.service;

import dataaccesslayer.DatenbankManagerInterface;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class EmailPruefer implements Pruefer {

    // Prüft, ob die E-Mail-Adresse die wichtigsten Regeln erfüllt
    public static boolean pruefe(String email) throws RemoteException  {
        if (!email.contains("@")) {
            Presenter.printError("E-Mail-Adresse muss ein '@' enthalten.");
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1);
        if (!domain.contains(".")) {
            Presenter.printError("E-Mail-Domain ist ungültig.");
            return false;
        }
        if (email.length() < 5 || email.length() > 50) {
            Presenter.printError("E-Mail-Adresse muss zwischen 5 und 50 Zeichen lang sein.");
            return false;
        }
        return true;
    }

    // Prüft, ob die E-Mail-Adresse schon in der Datenbank existiert
    public static boolean checkUniqueness(DatenbankManagerInterface db, String email) throws RemoteException {
        try {
            if(db.findeKundeNachEmail(email) != null) {
                return false;
            }
        } catch (RemoteException e) {
            Presenter.printError("Fehler bei Emailpruefung: Abgleichen mit Datenbank fehlgeschlagen.");
            return false;
        }
        return true;
    }

    // Fragt den Nutzer so lange nach einer E-Mail, bis eine gültige und freie Adresse eingegeben wurde
    public static String starteEmailPruefung(DatenbankManagerInterface db, Scanner scanner) throws RemoteException {
        String email;
        while (true) {
            Presenter.printMessage("Wie lautet Ihre E-Mail-Adresse: ");
            email = scanner.nextLine();

            if (!checkUniqueness(db, email)) {
                Presenter.printError("E-Mail-Adresse bereits vergeben.");
            } else if (pruefe(email)) {
                Presenter.printMessage("Gültige E-Mail-Adresse: " + email);
                return email;
            } else {
                Presenter.printError("Ungültige E-Mail-Adresse.");
            }
        }
    }
}
