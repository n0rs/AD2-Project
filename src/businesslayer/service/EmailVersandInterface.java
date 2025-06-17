package businesslayer.service;

import java.rmi.RemoteException;

public interface EmailVersandInterface extends java.rmi.Remote {
    void sendeRegistrierungsEmail(String token) throws RemoteException;
}
