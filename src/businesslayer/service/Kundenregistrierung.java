package businesslayer.service;

import businesslayer.objekte.Kunde;
import dataaccesslayer.DatenbankManagerInterface;
import presentationlayer.Presenter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Kundenregistrierung {
    public static Kunde registriereKunde(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner) throws MalformedURLException, RemoteException, NotBoundException {
        while(true) {
            String email = EmailPruefer.starteEmailPruefung(db, scanner);
            String passwort = PasswortPruefer.startePasswortPruefung(scanner);
            db.kundeAnlegen(email, passwort);
            Kunde kunde = db.findeKundeNachEmail(email);
            db.emailVerificationEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
            em.sendeRegistrierungsEmail(db.findeEmailTokenMitEmail(kunde.getEmail()), kunde.getEmail());
            return kunde;
        }
    }

    public static Kunde emailTokenDialog(DatenbankManagerInterface db, EmailVersandInterface em, Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
    Presenter.linkActivation();
    
    while (true) {
        try {
            int tokenChoice = Integer.parseInt(scanner.nextLine());
            if (tokenChoice == 1) {
                db.updateStatus(k.getId(), true);
                k = db.findeKundeNachEmail(k.getEmail());
                em.welcomeEmail(k.getEmail());
                return k;
            } else if (tokenChoice == 2) {
                Presenter.emailTokenAbgelaufen();
                db.kundenLoeschenId(k.getId());
                System.exit(0);
                return null;
            }
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
        }
    }
}
}
