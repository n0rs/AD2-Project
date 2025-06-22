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
        Presenter.introString();
        int op = chooseIntroOption(scanner);
        if(op == 1) {
        Kunde kunde = startRegistration(scanner);
        em.sendeRegistrierungsEmail(db.findeEmailTokenMitEmail(kunde.getEmail()), kunde.getEmail());
        kunde = tokenDialog(scanner, kunde);
        if (endDialog(scanner)) {
            db.verbindungTrennen();
            scanner.close(); // nur EINMAL am Ende schließen
            System.exit(0);
        }
    }
    if(op == 2) {
            Kunde kunde = passwordReset(scanner);
            db.updateStatus(kunde.getId(), false);
            em.passwortVergessen(db.findePasswortTokenMitEmail(kunde.getEmail()), kunde.getEmail());
            kunde = tokenDialog(scanner, kunde);
            PasswortVerwaltung.newPassword(kunde);
            kunde = db.findeKundeNachEmail(kunde.getEmail());
            System.out.println(kunde.toString());
    }
    if(op == 3) {
        Presenter.printMessage("Programm wird beendet.");
        db.verbindungTrennen();
        scanner.close(); // nur EINMAL am Ende schließen
        System.exit(0);
    }
}
}


public static Kunde passwordReset(Scanner scanner) throws RemoteException, MalformedURLException, NotBoundException {
   return PasswortVerwaltung.prepareReset();
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

public static Kunde startRegistration(Scanner scanner) throws RemoteException, MalformedURLException, NotBoundException {
    Presenter.signUpString();
    return Kundenregistrierung.registriereKunde(); // ggf. scanner mit übergeben
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
            if (continueChoice == 2) return true;
        } catch (NumberFormatException e) {
            Presenter.printError("Ungültige Eingabe. Bitte 1 oder 2 eingeben.");
        }
    }
}

public static Kunde tokenDialog(Scanner scanner, Kunde k) throws RemoteException, NotBoundException, MalformedURLException {
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
}