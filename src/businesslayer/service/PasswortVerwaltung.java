package businesslayer.service;

import businesslayer.objekte.*;
import dataaccesslayer.DatenbankManagerInterface;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import presentationlayer.Presenter;

public class PasswortVerwaltung {

    public static Kunde newPassword() throws RemoteException, MalformedURLException, NotBoundException {
    DatenbankManagerInterface db = (DatenbankManagerInterface) java.rmi.Naming.lookup("rmi://localhost:1099/DatenbankManager");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            Presenter.printMessage("Bitte geben Sie ein neues Passwort ein:\n");
            String newPW = scanner.nextLine();
            PasswortPruefer.startePasswortPruefung(scanner);
            return null;
        }
    }
}
