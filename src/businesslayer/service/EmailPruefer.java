    // Implementiert Pruefer
package businesslayer.service;

import dataaccesslayer.DatenbankManagerInterface;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class EmailPruefer implements Pruefer {
    @Override
    public boolean pruefe(String email) throws RemoteException, MalformedURLException, NotBoundException  {
        if (!email.contains("@")) {
            Presenter.printError("E-Mail-Adresse ist ungültig.");
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1);
        if (!domain.contains(".")) {
            return false;
        }
        if (email.length() < 5 || email.length() > 50) {
            return false;
        }
        return true;
    }

    
    public static boolean checkUniqueness(DatenbankManagerInterface db, String email) throws RemoteException, MalformedURLException, NotBoundException {
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


    // Führt eine Schleife durch, in der der Benutzer E-Mail-Adressen eingeben kann
    public static String starteEmailPruefung(DatenbankManagerInterface db, Scanner scanner) throws RemoteException, MalformedURLException, NotBoundException {
        String email;
        while (true) {
            Presenter.printMessage("Wie lautet Ihre E-Mail-Adresse: ");
            email = scanner.nextLine();

            EmailPruefer emailPruefer = new EmailPruefer(); // Erstellt ein neues Objekt vom Typ EmailPruefer zur Überprüfung der E-Mail
                if (checkUniqueness(db, email) == false) {
                    Presenter.printError("E-Mail-Adresse bereits vergeben. Bitte eine andere E-Mail-Adresse eingeben.");
                    continue;
                }
                if (emailPruefer.pruefe(email)) {
                    Presenter.printMessage("Gültige E-Mail-Adresse: " + email);
                    return email;
                } else {
                    Presenter.printError("Ungültige E-Mail-Adresse. Bitte versuchen Sie es erneut.");
                    continue;
                }
        }
    }
}
