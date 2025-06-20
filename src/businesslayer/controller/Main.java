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

    StartRegistry.startsRegistry(args);
    DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
    EmailVersandInterface em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");

    while (true) {
        Kunde kunde = start(scanner);
        em.sendeRegistrierungsEmail(db.findeEmailTokenMitEmail(kunde.getEmail()), kunde.getEmail());
        kunde = tokenDialog(scanner, kunde);
        if (endDialog(scanner)) {
            db.verbindungTrennen();
            scanner.close(); // nur EINMAL am Ende schließen
            System.exit(0);
        }
    }
}

public static Kunde start(Scanner scanner) throws RemoteException, MalformedURLException, NotBoundException {
    Presenter.introString();
    return Kundenregistrierung.registriereKunde(); // ggf. scanner mit übergeben
}

public static boolean endDialog(Scanner scanner) {
    Presenter.printMessage("Möchten Sie fortfahren? \n1. Ja\n2. Nein");
    while (true) {
        try {
            int continueChoice = Integer.parseInt(scanner.nextLine());
            if (continueChoice == 1) {
                Presenter.printMessage("Sie wurden ausgeloggt. \nNeustart:\n");
                return false;
            }
            if (continueChoice == 2) return true;
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
        }
    }
}

public static Kunde tokenDialog(Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
    DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
    EmailVersandInterface em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
    Presenter.finalizeRegistrierung();
    
    while (true) {
        try {
            int tokenChoice = Integer.parseInt(scanner.nextLine());
            if (tokenChoice == 1) {
                db.updateStatus(k.getId());
                k = db.findeKundeNachEmail(k.getEmail());
                em.welcomeEmail(k.getEmail());
                return k;
            } else if (tokenChoice == 2) {
                Presenter.tokenAbgelaufen();
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