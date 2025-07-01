package businesslayer.service;

import businesslayer.objekte.*;
import dataaccesslayer.DatenbankManagerInterface;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class PasswortVerwaltung {

    // Setzt das Passwort eines Kunden neu
    public static Kunde newPassword(DatenbankManagerInterface db, Scanner scanner, Kunde kunde) throws RemoteException, MalformedURLException, NotBoundException {
        int kundenId = kunde.getId();
        String newPW = PasswortPruefer.startePasswortPruefung(scanner); // Fragt neues Passwort ab
        db.updatePassword(kundenId, newPW); // Speichert das neue Passwort in der Datenbank
        return db.findeKundeNachID(kundenId); // Gibt den aktualisierten Kunden zurück
    }

    // Startet den Ablauf zum Zurücksetzen des Passworts
    public static Kunde passwortReset (DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde kunde) throws MalformedURLException, RemoteException, NotBoundException {
        String token = TokenErstellung.erstelleToken(); // Erstellt ein neues Token für das Zurücksetzen
        String email;
        if (kunde == null) {
            // Wenn kein Kunde angemeldet ist, E-Mail abfragen
            while (true) {
                Presenter.printMessage("Bitte geben Sie Ihre E-Mail-Adresse ein:\n");
                email = scanner.nextLine();
                if (EmailPruefer.checkUniqueness(db, email) == false) {
                    // E-Mail existiert, Kunde wird geladen
                    kunde = db.findeKundeNachEmail(email);
                    System.err.println(kunde);
                } else {
                    // E-Mail nicht gefunden
                    Presenter.printError("E-Mail-Adresse nicht gefunden.");
                    return null;
                }
                // Token für Passwort-Reset speichern und E-Mail versenden
                db.passwortResetEintragErstellen(kunde.getId(), token);
                em.passwortVergessen(token, kunde.getEmail());
                // Dialog für die Eingabe des Tokens
                kunde = passwortTokenDialog(db, scanner, kunde);
                if (kunde == null) {
                    Presenter.printError("Token abgelaufen oder ungültig. Bitte versuchen Sie es erneut.");
                    return null;
                }
                else {
                    // Neues Passwort setzen
                    kunde = newPassword(db, scanner, kunde);
                    Presenter.printMessage("Passwort erfolgreich zurückgesetzt.");
                }
                return kunde;
            }
        }
        else {
            // Wenn Kunde schon angemeldet ist
            db.passwortResetEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
            em.passwortVergessen(db.findePasswortTokenMitEmail(kunde.getEmail()), kunde.getEmail());
            kunde = passwortTokenDialog(db, scanner, kunde);
            if (kunde == null) {
                Presenter.printError("Token abgelaufen oder ungültig. Bitte versuchen Sie es erneut.");
                return null;
            } else {
                kunde = newPassword(db, scanner, kunde);
                Presenter.printMessage("Passwort erfolgreich zurückgesetzt.");
            }
            return kunde;
        }
    }

    // Dialog für die Eingabe des Passwort-Tokens
    public static Kunde passwortTokenDialog(DatenbankManagerInterface db, Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
        Presenter.linkActivation();
        while (true) {
            try {
                int tokenChoice = Integer.parseInt(scanner.nextLine());
                if (tokenChoice == 1) {
                    // Token bestätigt, Kunde wird geladen und Token gelöscht
                    k = db.findeKundeNachEmail(k.getEmail());
                    db.loeschePasswortToken(k.getId());
                    return k;
                } if (tokenChoice == 2) {
                    // Token abgelaufen oder abgebrochen
                    Presenter.passwortTokenAbgelaufen();
                    db.loeschePasswortToken(k.getId());
                    return null;
                }
                else {
                    Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
                    continue;
                }
            } catch (NumberFormatException e) {
                Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
                continue; 
            }
        }
    }
}
