package businesslayer.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import businesslayer.service.EmailVersand;
import dataaccesslayer.DatenbankManager;
import presentationlayer.Presenter;

public class StartRegistry {
    public static void startsRegistry(String[] args) throws RemoteException, AlreadyBoundException {
        Registry reg;
        int p = 1099;
        reg = java.rmi.registry.LocateRegistry.createRegistry(p);
        Presenter.printMessage("RMI-Registry gestartet auf Port " + p);
        DatenbankManager datenbankManager = new DatenbankManager();
        EmailVersand emailVersand = new EmailVersand();
        reg.bind("DatenbankManager", datenbankManager);
        reg.bind("EmailVersand", emailVersand);
    }
}

