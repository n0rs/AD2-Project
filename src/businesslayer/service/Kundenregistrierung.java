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
            db.kundeAnlegen(email, passwort);
            Kunde kunde = db.findeKundeNachEmail(email);
            return kunde;
        }
    }
}
