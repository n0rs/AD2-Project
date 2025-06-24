package businesslayer.service;

import businesslayer.objekte.*;
import dataaccesslayer.DatenbankManagerInterface;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class PasswortVerwaltung {
    public static Kunde newPassword(DatenbankManagerInterface db, Scanner scanner, Kunde kunde) throws RemoteException, MalformedURLException, NotBoundException {
        int kundenId = kunde.getId();
        Presenter.printMessage("Passwort zurücksetzen für Kunde mit ID: " + kundenId);
        String newPW = PasswortPruefer.startePasswortPruefung(scanner);
        db.updatePassword(kundenId, newPW);
        return db.findeKundeNachID(kundenId);
    }

    public static Kunde passwortReset (DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde kunde) throws MalformedURLException, RemoteException, NotBoundException {
        String token = TokenErstellung.erstelleToken();
        String email;
        if (kunde == null) {
            while (true) {
                Presenter.printMessage("Bitte geben Sie Ihre E-Mail-Adresse ein:\n");
                email = scanner.nextLine();
                if (EmailPruefer.checkUniqueness(db, email) == false) {
                    kunde = db.findeKundeNachEmail(email);
                } else {
                    Presenter.printError("E-Mail-Adresse nicht gefunden.");
                    return null;
                    // Wenn die E-Mail nicht existiert, gehe zurück zum Anfang
                }
                db.passwortResetEintragErstellen(kunde.getId(), token);
                em.passwortVergessen(token, kunde.getEmail());
                kunde = passwortTokenDialog(db, scanner, kunde);
                if (kunde == null) {
                    Presenter.printError("Token abgelaufen oder ungültig. Bitte versuchen Sie es erneut.");
                    return null; // Wenn der Token abgelaufen ist, gehe zurück zum Anfang
                }
                else {
                    kunde = newPassword(db, scanner, kunde);
                    Presenter.printMessage("Passwort erfolgreich zurückgesetzt.");
                }
                return kunde;
            }
        }
        else {
            db.passwortResetEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
            em.passwortVergessen(db.findePasswortTokenMitEmail(kunde.getEmail()), kunde.getEmail());
            kunde = passwortTokenDialog(db, scanner, kunde);
            if (kunde == null) {
                Presenter.printError("Token abgelaufen oder ungültig. Bitte versuchen Sie es erneut.");
                return null; // Wenn der Token abgelaufen ist, gehe zurück zum Anfang
            } else {
                kunde = newPassword(db, scanner, kunde);
                Presenter.printMessage("Passwort erfolgreich zurückgesetzt.");
            }
            return kunde;
        }
    }

    public static Kunde passwortTokenDialog(DatenbankManagerInterface db, Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
    Presenter.linkActivation();
    while (true) {
        try {
            int tokenChoice = Integer.parseInt(scanner.nextLine());
            if (tokenChoice == 1) {
                k = db.findeKundeNachEmail(k.getEmail());
                db.loeschePasswortToken(k.getId());
                return k;
            } else if (tokenChoice == 2) {
                Presenter.passwortTokenAbgelaufen();
                db.loeschePasswortToken(k.getId());
                return null;
            }
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
        }
    }
}
}
