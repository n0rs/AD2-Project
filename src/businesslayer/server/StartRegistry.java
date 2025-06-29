package businesslayer.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import businesslayer.service.EmailVersand;
import dataaccesslayer.DatenbankManager;
import presentationlayer.Presenter;

public class StartRegistry {
    // Startet die RMI-Registry und meldet die Dienste an
    public static void startsRegistry(String[] args) throws RemoteException, AlreadyBoundException {
        final int PORT = 1099;
        try {
            // Startet die Registry auf dem angegebenen Port
            Registry reg = java.rmi.registry.LocateRegistry.createRegistry(PORT);
            Presenter.printMessage("RMI-Registry gestartet auf Port " + PORT);

            // Erstellt die Objekte für die Datenbank und den E-Mail-Versand
            DatenbankManager datenbankManager = new DatenbankManager();
            EmailVersand emailVersand = new EmailVersand();

            // Meldet die Dienste in der Registry an, damit der Client darauf zugreifen können
            reg.bind("DatenbankManager", datenbankManager);
            reg.bind("EmailVersand", emailVersand);
        } catch (RemoteException e) {
            // Fehler beim Starten der Registry
            Presenter.printError("Fehler beim Starten der RMI-Registry: " + e.getMessage());
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            // Ein Dienst ist bereits angemeldet
            Presenter.printError("Ein Service ist bereits registriert: " + e.getMessage());
        }
    }
}