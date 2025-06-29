package businesslayer.service;

import businesslayer.objekte.Kunde;
import dataaccesslayer.DatenbankManagerInterface;
import presentationlayer.Presenter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Kundenregistrierung {

    // Führt die Registrierung eines neuen Kunden durch
    public static Kunde registriereKunde(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner) throws MalformedURLException, RemoteException, NotBoundException {
        String token = TokenErstellung.erstelleToken(); // Erstellt ein neues Token für die E-Mail-Bestätigung
        String email = EmailPruefer.starteEmailPruefung(db, scanner); 
        String passwort = PasswortPruefer.startePasswortPruefung(scanner); 
        db.kundeAnlegen(email, passwort); 
        Kunde kunde = db.findeKundeNachEmail(email); // Holt den Kunden aus der Datenbank
        db.emailVerificationEintragErstellen(kunde.getId(), token); // Speichert das Token zur Verifizierung
        em.sendeRegistrierungsEmail(token, kunde.getEmail()); // Schickt die E-Mail mit dem Bestätigungslink
        return kunde;
    }

    // Dialog für die E-Mail-Bestätigung nach der Registrierung
    public static Kunde emailTokenDialog(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
        Presenter.linkActivation();
        while (true) {
            try {
                int tokenChoice = Integer.parseInt(scanner.nextLine());
                if (tokenChoice == 1) {
                    db.updateStatus(k.getId(), true);
                    k = db.findeKundeNachEmail(k.getEmail());
                    em.welcomeEmail(k.getEmail());
                    db.loescheEmailToken(k.getId());
                    return k;
                } else if (tokenChoice == 2) {
                    Presenter.emailTokenAbgelaufen(); // Hinweis, dass das Token abgelaufen ist
                    db.kundenLoeschenId(k.getId()); // Löscht den Kunden aus der Datenbank
                    System.exit(0); // Beendet das Programm
                    return null;
                }
            } catch (NumberFormatException e) {
                Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
            }
        }
    }
}
