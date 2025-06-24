package businesslayer.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import businesslayer.service.EmailVersand;
import dataaccesslayer.DatenbankManager;
import presentationlayer.Presenter;

public class StartRegistry {
    public static void startsRegistry(String[] args) throws RemoteException, AlreadyBoundException {
        final int PORT = 1099;
        try {
            Registry reg = java.rmi.registry.LocateRegistry.createRegistry(PORT);
            Presenter.printMessage("RMI-Registry gestartet auf Port " + PORT);

            DatenbankManager datenbankManager = new DatenbankManager();
            EmailVersand emailVersand = new EmailVersand();

            reg.bind("DatenbankManager", datenbankManager);
            reg.bind("EmailVersand", emailVersand);
        } catch (RemoteException e) {
            Presenter.printError("Fehler beim Starten der RMI-Registry: " + e.getMessage());
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            Presenter.printError("Ein Service ist bereits registriert: " + e.getMessage());
        }
}
}