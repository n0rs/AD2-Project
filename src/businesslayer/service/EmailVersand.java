package businesslayer.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import presentationlayer.Presenter;

public class EmailVersand extends UnicastRemoteObject implements EmailVersandInterface {

    public EmailVersand() throws RemoteException {
        super();
    }

    @Override
    public void sendeRegistrierungsEmail(String token, String empfaenger) throws RemoteException {
        String betreff = "Aktivierungslink";
        String text = """
                Vielen Dank für Ihre Registrierung. Wir freuen uns, Sie als neuen Kunden begrüßen zu dürfen.
                Bitte schließen Sie Ihre Registrierung ab, indem Sie auf den folgenden Token klicken:
                %s
                """.formatted(token);
        Presenter.printEmail(empfaenger, betreff, text);
    }

    @Override
    public void welcomeEmail(String empfaenger) throws RemoteException {
        String betreff = "Willkommen";
        String text = """
                Willkommen im Webshop! Ihr Account wurde bestätigt.
                """;
        Presenter.printEmail(empfaenger, betreff, text);
    }

    @Override
    public void passwortVergessen(String token, String empfaenger) throws RemoteException {
        String betreff = "Aktivierungslink";
        String text = """
                Passwort vergessen? Bitte klicken Sie innerhalb von einer Stunde auf den folgenden Token,
                um Ihr Passwort zurückzusetzen:
                %s
                """.formatted(token);
        Presenter.printEmail(empfaenger, betreff, text);
    }
}
