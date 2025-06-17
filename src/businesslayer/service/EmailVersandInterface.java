package businesslayer.service;

public interface EmailVersandInterface extends java.rmi.Remote {
    void sendeRegistrierungsEmail(String token) throws java.rmi.RemoteException;
}
