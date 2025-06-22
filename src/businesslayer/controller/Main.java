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

    Kunde kunde;
    StartRegistry.startsRegistry(args);
    DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
    EmailVersandInterface em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
    db.verbindungAufbauen();

    while (true) {
        Presenter.introString();
        int op = chooseIntroOption(scanner);
        if(op == 1) {
            kunde = Kundenregistrierung.registriereKunde(scanner);
            em.sendeRegistrierungsEmail(db.findeEmailTokenMitEmail(kunde.getEmail()), kunde.getEmail());
            kunde = emailTokenDialog(scanner, kunde);
            if (endDialog(scanner)) {
                db.verbindungTrennen();
                System.exit(0);
            }
            }
        if(op == 2) {
            kunde = PasswortVerwaltung.prepareReset(scanner);
            db.passwortResetEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
            db.updateStatus(kunde.getId(), false);
            em.passwortVergessen(db.findePasswortTokenMitEmail(kunde.getEmail()), kunde.getEmail());
            kunde = passwortTokenDialog(scanner, kunde);
            if (kunde == null) {
                continue; // Wenn der Token abgelaufen ist, gehe zurück zum Anfang
            } else {
                PasswortVerwaltung.newPassword(kunde, scanner);
                kunde = db.findeKundeNachEmail(kunde.getEmail());
                System.out.println(kunde.toString());  
            }
              
        }
        if(op == 3) {
            Presenter.printMessage("Programm wird beendet.");
            db.verbindungTrennen();
            System.exit(0);
        }
    }
}



public static int chooseIntroOption(Scanner scanner) {
    while (true) {
        try {
            int opt = Integer.parseInt(scanner.nextLine());
            if(opt < 1 | opt > 2) {
                continue;
            }
            return opt;
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
            return -1;
        }
    }
}

public static boolean endDialog(Scanner scanner) {
    Presenter.hauptmenuString();
    while (true) {
        try {
            int continueChoice = Integer.parseInt(scanner.nextLine());
            if (continueChoice == 1) {
                Presenter.printMessage("Sie wurden ausgeloggt. \nNeustart:\n");
                return false;
            }
            if (continueChoice == 2) {
                return false;
            }
            if (continueChoice == 3) {
                Presenter.printMessage("Programm wird beendet.");
                return true;
            } else {
                Presenter.printError("Ungültige Eingabe. Bitte 1, 2 oder 3 eingeben.");
                continue;
            }
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
        }
    }
}

public static Kunde emailTokenDialog(Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
    DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
    EmailVersandInterface em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
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

public static Kunde passwortTokenDialog(Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
    DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
    Presenter.linkActivation();
    
    while (true) {
        try {
            int tokenChoice = Integer.parseInt(scanner.nextLine());
            if (tokenChoice == 1) {
                k = db.findeKundeNachEmail(k.getEmail());
                return k;
            } else if (tokenChoice == 2) {
                Presenter.tokenAbgelaufen();
                return null;
            }
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
        }
    }
}
}