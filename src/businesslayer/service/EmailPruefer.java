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
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1);
        if (!domain.contains(".")) {
            return false;
        }
        if (email.length() < 5 || email.length() > 50) {
            return false;
        }
        if(checkUniqueness(email, null) == false) {
            
            return false;
        }
        return true;
    }

    @Override
    public boolean checkUniqueness(String email, String wert) throws RemoteException, MalformedURLException, NotBoundException {
        
        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        try {
             if(db.findeKundeNachEmail(email) != null) {
                return false;
            }
          } catch (RemoteException e) {
            Presenter.printError("Fehler bei Emailpruefung: Abgleichen mit Datenbank fehlgeschlagen.");
        }
        return true;
    }

    // Führt eine Schleife durch, in der der Benutzer E-Mail-Adressen eingeben kann
    public static String starteEmailPruefung(Scanner scanner) throws RemoteException, MalformedURLException, NotBoundException {
        String email;
        while (true) {
            Presenter.printMessage("Wie lautet Ihre E-Mail-Adresse: ");
            email = scanner.nextLine();

            EmailPruefer EmailPruefer = new EmailPruefer(); // Erstellt ein neues Objekt vom Typ EmailPruefer zur Überprüfung der E-Mail
            if (EmailPruefer.pruefe(email)) { // Ruft die Methode "pruefe" auf und prüft die eingegebene E-Mail
                Presenter.printMessage("Gültige E-Mail-Adresse: " + email);
                break;
            } else {
                Presenter.printError("Ungültige E-Mail-Adresse. Bitte versuchen Sie es erneut.");
            }
        }
        return email;
    }
}
