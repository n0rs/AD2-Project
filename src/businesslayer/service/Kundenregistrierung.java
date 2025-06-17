package businesslayer.service;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

import businesslayer.objekte.Kunde;
import dataaccesslayer.DatenbankManagerInterface;

public class Kundenregistrierung {
    public static Kunde registriereKunde() throws MalformedURLException, RemoteException, NotBoundException {
        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String email = EmailPruefer.starteEmailPruefung(scanner);
            String passwort = PasswortPruefer.startePasswortPruefung(scanner);
            db.verbindungAufbauen();
            db.kundeAnlegen(email, passwort);
            Kunde kunde = db.findeKundeNachEmail(email);
            db.emailVerificationEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
            db.passwortResetEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
            return kunde;
        }
    }
}
