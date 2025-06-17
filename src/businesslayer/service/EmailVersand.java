package businesslayer.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EmailVersand extends UnicastRemoteObject implements EmailVersandInterface {
    public EmailVersand() throws RemoteException {
        super();
    }

    @Override
    public void sendeRegistrierungsEmail(String token, String empfaenger) throws RemoteException {
        String email = 
                       "Empfänger: " + empfaenger + "\nBetreff: Aktivierungslink \nVielen Dank f\u00fcr Ihre Registrierung. Wir freuen uns, Sie als neuen Kunden begr\u00fc\u00dfen zu d\u00fcrfen. Bitte schlie\u00dfen Sie Ihre Registrierung ab, indem Sie auf den folgenden token klicken:\n"
                       + token + "\n";
        System.out.println(email);
    }

    @Override
    public void welcomeEmail(String empfaenger) throws RemoteException {
        String welcome =  "Empfänger: " + empfaenger + "\nBetreff: Willkommen \n Willkommen im Webshop! Ihre Account wurde bestätigt";
        ;
        System.out.println(welcome);
    }

    @Override
    public void passwortVergessen(String token, String empfaenger) throws RemoteException {
        String forgotpw = "Empfänger: \" + empfaenger + \"\\n" + //
                        "Betreff: Aktivierungslink \\n" + //
                        "Passwort vergessen? Bitte klicken Sie innerhalb von einer Stunde auf den folgenden token, um Ihr Passwort zurückzusetzen: \n " + token;
        System.out.println(forgotpw);
    }
}
