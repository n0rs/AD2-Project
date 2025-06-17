package businesslayer.controller;

import businesslayer.objekte.Kunde;
import businesslayer.service.*;
import businesslayer.server.StartRegistry;
import dataaccesslayer.*;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
        // Starten des RMI-Registry
        StartRegistry.startsRegistry(args);
        // Lookup des Datenbankmanagers um seine Methoden nutzen zu können
        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        EmailVersandInterface em = (EmailVersandInterface) java.rmi.Naming.lookup("rmi://localhost:1099/EmailVersand");
        
        Kunde kunde = start();
        em.sendeRegistrierungsEmail(db.findeEmailTokenMitEmail(kunde.getEmail()));
        db.verbindungTrennen();
        System.exit(0);
    }

    public static Kunde start() throws RemoteException, MalformedURLException, NotBoundException {
        System.out.println("""
            
        Willkommen bei der Kunderegistrierung!
        Bitte geben Sie Ihre E-Mail-Adresse und Ihr Passwort ein, um sich zu registrieren.
        
                """);
        Kunde kunde = Kundenregistrierung.registriereKunde();
        System.out.println(kunde);
        return kunde;
    }
}