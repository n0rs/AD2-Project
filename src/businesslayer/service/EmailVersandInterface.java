package businesslayer.service;

import java.rmi.RemoteException;

public interface EmailVersandInterface extends java.rmi.Remote {
    void sendeRegistrierungsEmail(String token, String empfaenger) throws RemoteException;
    void welcomeEmail(String empfaenger) throws RemoteException;
    void passwortVergessen(String token, String empfaenger) throws RemoteException;
}
