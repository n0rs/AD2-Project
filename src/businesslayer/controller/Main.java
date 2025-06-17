package businesslayer.controller;

import businesslayer.objekte.Kunde;
import businesslayer.service.*;
import businesslayer.server.StartRegistry;
import dataaccesslayer.*;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, AlreadyBoundException {
    // Starten des RMI-Registry
    StartRegistry.startsRegistry(args);
    try (Scanner scanner = new Scanner(System.in)) {
        String email;
        String passwort;
        // Verbindung zur Datenbank herstellen und eine Abfrage durchführen
        DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        db.verbindungAufbauen();

        email = EmailPruefer.starteEmailPruefung(scanner);
        passwort = PasswortPruefer.startePasswortPruefung(scanner);

        Kunde kunde = new Kunde(email, passwort);
        Kunde kunde2 = new Kunde("max@beispiel.de", "Passwort123!");
        db.kundeAnlegen(kunde.getEmail(), kunde.getPassword());
        db.kundeAnlegen(kunde2.getEmail(), kunde2.getPassword());
        kunde = db.findeKundeNachEmail(kunde.getEmail());
        kunde2 = db.findeKundeNachEmail(kunde2.getEmail());
        System.out.println(kunde.getId());
        System.out.println(kunde2.getId());
        db.emailVerificationEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
        db.passwortResetEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
        db.emailVerificationEintragErstellen(kunde2.getId(), TokenErstellung.erstelleToken());
        db.passwortResetEintragErstellen(kunde2.getId(), TokenErstellung.erstelleToken());

        System.out.println(db.findeEmailTokenMitEmail(email)); 
        System.out.println(db.findePasswortTokenMitEmail(email));


        db.verbindungTrennen();
        System.exit(0);
    }
    }
}