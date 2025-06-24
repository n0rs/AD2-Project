// Implementiert Pruefer
package businesslayer.service;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class PasswortPruefer implements Pruefer {

    public static boolean pruefe(String password) {
        if (password.length() < 8) return false;
        // Nutzt regular expressions um zu prüfen ob das Passwort mindestens einen Buchstaben, eine Zahl und ein Sonderzeichen enthält
        // .* bedeutet beliebig viele Zeichen, [a-zA-Z] bedeutet ein Buchstabe, \\d bedeutet eine Zahl und [!@#$%^&*(),.?":{}|<>] bedeutet ein Sonderzeichen
        // + bedeutet mindestens einmal
        boolean hasLetter = password.matches(".*[a-zA-Z]+.*");
        boolean hasDigit = password.matches(".*\\d+.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>]+.*");
        return hasLetter && hasDigit && hasSpecial;
    }

    /*@Override
    public boolean checkUniqueness(String password, String email) throws RemoteException, MalformedURLException, NotBoundException {
        
        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        try {
             if(db.findeKundeNachPasswort(password) != null) {
                String compareMail = db.findeKundeNachPasswort(password).getEmail();
                if(compareMail.equals(email)) {
                return false; 
          }
        }
         } catch (RemoteException e) {
            Presenter.printError("Fehler bei Passwortpruefung: Abgleichen mit Datenbank fehlgeschlagen.");
        }
        return true;
    }*/


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