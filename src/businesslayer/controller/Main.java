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
private static final String ungueltigeEingabe = "Ungültige Eingabe. Bitte 1, 2 oder 3 eingeben.";
private static final String programmBeenden = "Programm wird beendet.";

public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {

    // Scanner für die Benutzereingabe
    Scanner scanner = new Scanner(System.in); // einmaliger Scanner
    Kunde kunde = null;
    // Startet die RMI-Registry
    StartRegistry.startsRegistry(args);
    DatenbankManagerInterface db = null;
    EmailVersandInterface em = null;
    try {
        // Verbindet sich mit den entfernten Objekten (Server)
        db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
    } catch (NotBoundException | MalformedURLException | RemoteException e) {
        Presenter.printError("Verbindung zum Server fehlgeschlagen." + programmBeenden);
        System.exit(0);
    } 
    // Baut die Verbindung zur Datenbank auf
    db.verbindungAufbauen();
    // Startet den Dialog mit dem Benutzer
    startDialog(db, em, scanner, kunde);
}

// Hauptdialog für die Benutzerinteraktion
public static void startDialog(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde kunde) throws MalformedURLException, RemoteException, NotBoundException {
    while (true) {
        Presenter.introString(); // Begrüßung und Menü anzeigen
        try {int op = Integer.parseInt(scanner.nextLine());
            if (op == 1) {
                // Registrierung eines neuen Kunden
                kunde = Kundenregistrierung.registriereKunde(db, em, scanner);
                // E-Mail-Bestätigung und Aktivierung
                kunde = Kundenregistrierung.emailTokenDialog(db, em, scanner, kunde);
                // Nach erfolgreicher Registrierung ins Hauptmenü
                kunde = hauptMenuDialog(db, em, scanner, kunde);
                continue;
            }
            if (op == 2) {
                // Passwort zurücksetzen
                kunde = PasswortVerwaltung.passwortReset(db, em, scanner, kunde);
                if (kunde == null) {
                    Presenter.printError("Passwort-Reset fehlgeschlagen. Bitte versuchen Sie es erneut.");
                    continue; 
                } else {
                    continue;
                }
            }
            if (op == 3) {
                // Programm beenden
                Presenter.printMessage(programmBeenden);
                System.exit(0);
            } else {
                Presenter.printError(ungueltigeEingabe);
                continue;
            }
        } catch (NumberFormatException e) {
            Presenter.printError(ungueltigeEingabe);
            continue;
        }
    }
}

// Menü nach erfolgreicher Anmeldung/Registrierung
public static Kunde hauptMenuDialog(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde kunde) throws MalformedURLException, RemoteException, NotBoundException {
    Presenter.hauptmenuString(); // Hauptmenü anzeigen
    while (true) {
        try {
            String email = kunde.getEmail();
            int continueChoice = Integer.parseInt(scanner.nextLine());
            if (continueChoice == 1) {
                // Ausloggen, Kunde wird wieder auf null gesetzt
                Presenter.printMessage("Sie wurden ausgeloggt. \nNeustart:\n");
                return null;
            }
            if (continueChoice == 2) {
                // Passwort zurücksetzen
                kunde = PasswortVerwaltung.passwortReset(db, em, scanner, kunde);
                if (kunde == null) {
                    // Falls das Zurücksetzen fehlschlägt, bleibt der Kunde angemeldet
                    kunde = db.findeKundeNachEmail(email);
                    Presenter.printError("Passwort-Reset fehlgeschlagen. Bitte versuchen Sie es erneut.");
                return kunde; 
                } else {
                    Presenter.printMessage("Passwort erfolgreich zurückgesetzt.");  
                }
                return kunde;
            }
            if (continueChoice == 3) {
                // Programm beenden
                Presenter.printMessage(programmBeenden);
                System.exit(0);
            } else {
                Presenter.printError(ungueltigeEingabe);
                continue;
            }
        } catch (NumberFormatException e) {
            // Fehler bei der Eingabe (keine Zahl)
            Presenter.printError(ungueltigeEingabe);
            continue;
        }
    }
}
}