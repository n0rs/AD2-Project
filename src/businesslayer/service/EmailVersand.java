package businesslayer.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EmailVersand extends UnicastRemoteObject implements EmailVersandInterface {
    public EmailVersand() throws RemoteException {
        super();
    }

    public void sendeRegistrierungsEmail(String token) throws RemoteException {
        String email = "Vielen Dank für Ihre Registrierung. Wir freuen uns, Sie als neuen Kunden begrüßen zu dürfen.\n" +
                       "Bitte schließen Sie Ihre Registrierung ab, indem Sie auf den folgenden token klicken:\n" + token + "\n";
        System.out.println(email);
    }
}
