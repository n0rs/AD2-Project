package businesslayer.service;

import businesslayer.objekte.*;
import dataaccesslayer.DatenbankManagerInterface;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class PasswortVerwaltung {

    public static Kunde newPassword(Kunde kunde) throws RemoteException, MalformedURLException, NotBoundException {
    DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        try (Scanner scanner = new Scanner(System.in)) {
            int kundenId = kunde.getId();
            while(true) {
                Presenter.printMessage("Passwort zurücksetzen für Kunde mit ID: " + kundenId);
                String newPW = PasswortPruefer.startePasswortPruefung(scanner);
                db.updatePassword(kundenId, newPW);
                return db.findeKundeNachID(kundenId);
            }
        }
    }

    public static Kunde prepareReset() throws RemoteException, MalformedURLException, NotBoundException {
        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        try (Scanner scanner = new Scanner(System.in)) {
            String email;
                while (true) {
                    Presenter.printMessage("Bitte geben Sie Ihre E-Mail-Adresse ein:\n");
                    email = scanner.nextLine();
                    if(EmailPruefer.checkUniqueness(email) == false) {
                        return db.findeKundeNachEmail(email);
                    } else {
                        Presenter.printError("E-Mail-Adresse nicht gefunden.");
                        return null;
                    }
                }
        }
    }
}
