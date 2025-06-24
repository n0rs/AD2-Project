package businesslayer.controller;

import businesslayer.objekte.Kunde;
import businesslayer.server.StartRegistry;
import businesslayer.service.*;
import dataaccesslayer.*;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.Scanner;
import presentationlayer.Presenter;

public class Main {
public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
    Scanner scanner = new Scanner(System.in); // einmaliger Scanner
    Kunde kunde = null;
    StartRegistry.startsRegistry(args);
    DatenbankManagerInterface db = null;
    EmailVersandInterface em = null;
    try {
        db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
    } catch (NotBoundException | MalformedURLException | RemoteException e) {
        Presenter.printError("Verbindung zum Server fehlgeschlagen.");
        return;
    } 
    db.verbindungAufbauen();
    startDialog(db, em, scanner, kunde);
}

public static void startDialog(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde kunde) throws MalformedURLException, RemoteException, NotBoundException {
    while (true) {
        Presenter.introString();
        int op = Integer.parseInt(scanner.nextLine());
        if (op == 1) {
            kunde = Kundenregistrierung.registriereKunde(db, em, scanner);
            kunde = Kundenregistrierung.emailTokenDialog(db, em, scanner, kunde);
            hauptMenuDialog(db, em, scanner, kunde);
            continue;
        }
        if (op == 2) {
            kunde = PasswortVerwaltung.passwortReset(db, em, scanner, kunde);
            if (kunde == null) {
                Presenter.printError("Passwort-Reset fehlgeschlagen. Bitte versuchen Sie es erneut.");
                continue; // Gehe zurück zum Anfang, um eine neue Eingabe zu ermöglichen
            } else {
                Presenter.printMessage("Passwort erfolgreich zurückgesetzt.");
                continue;
            }
        } else {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
            continue;
        }
    }
}

public static void hauptMenuDialog(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde kunde) throws MalformedURLException, RemoteException, NotBoundException {
    Presenter.hauptmenuString();
    while (true) {
        try {
            String email = kunde.getEmail();
            int continueChoice = Integer.parseInt(scanner.nextLine());
            if (continueChoice == 1) {
                Presenter.printMessage("Sie wurden ausgeloggt. \nNeustart:\n");
                return;
            }
            if (continueChoice == 2) {
                kunde = PasswortVerwaltung.passwortReset(db, em, scanner, kunde);
                if (kunde == null) {
                    kunde = db.findeKundeNachEmail(email);
                    Presenter.printError("Passwort-Reset fehlgeschlagen. Bitte versuchen Sie es erneut.");
                return; // Gehe zurück zum Anfang, um eine neue Eingabe zu ermöglichen
                } else {
                    Presenter.printMessage("Passwort erfolgreich zurückgesetzt.");  
                }
                return;
            }
            if (continueChoice == 3) {
                Presenter.printMessage("Programm wird beendet.");
                System.exit(0);
            } else {
                Presenter.printError("Ungültige Eingabe. Bitte 1, 2 oder 3 eingeben.");
                continue;
            }
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1, 2 oder 3 eingeben.");
        }
    }
}

}