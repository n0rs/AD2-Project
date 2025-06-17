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
        // Starten des RMI-Registry
        StartRegistry.startsRegistry(args);
        // Lookup des Datenbankmanagers um seine Methoden nutzen zu können
        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        EmailVersandInterface em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
        
        Kunde kunde = start();
        em.sendeRegistrierungsEmail(db.findeEmailTokenMitEmail(kunde.getEmail()), kunde.getEmail());
        kunde = tokenDialog(kunde);
        db.verbindungTrennen();
        System.exit(0);
    }

    public static Kunde start() throws RemoteException, MalformedURLException, NotBoundException {
        Presenter.introString();
        Kunde kunde = Kundenregistrierung.registriereKunde();
        return kunde;
    }


    public static Kunde tokenDialog(Kunde k) throws RemoteException, NotBoundException, MalformedURLException {

        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        EmailVersandInterface em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
        Presenter.finalizeRegistrierung();
        Scanner scanner = new Scanner(System.in);

        while(true) {
            int tokenChoice = scanner.nextInt();
            if(tokenChoice == 1) {
                db.updateStatus(k.getId());
                k = db.findeKundeNachEmail(k.getEmail());
                em.welcomeEmail(k.getEmail());
                return k;
            }
            if(tokenChoice == 2) {
                Presenter.tokenAbgelaufen();
                db.kundenLoeschenId(k.getId());
                System.out.println("hat geklappt");
                return null;
            } 
        }
    }
}