package dataaccesslayer;


import java.rmi.RemoteException;
import java.util.List;

import businesslayer.objekte.Kunde;

public interface DatenbankManagerInterface extends java.rmi.Remote {

    public abstract void verbindungAufbauen() throws RemoteException;
    public abstract void verbindungTrennen() throws RemoteException;
    public abstract void kundeAnlegen(String email, String passwort) throws RemoteException;
    public abstract void kundenLoeschenIds(List<Integer> ids) throws RemoteException;
    public abstract int findeKundenId(String email) throws RemoteException;
    public abstract void emailVerificationEintragErstellen(int kundenId, String token) throws RemoteException;
    public abstract List<Integer> abgelaufeneEmailTokenFinden() throws RemoteException;
    public abstract void passwortResetEintragErstellen(int kundenId, String token) throws RemoteException;
    public abstract List<Integer> abgelaufenePasswortTokenfinden() throws RemoteException;
    public abstract Kunde findeKundeNachEmail(String email) throws RemoteException;
    public abstract String findeEmailTokenMitEmail(String email) throws RemoteException;
    public abstract String findePasswortTokenMitEmail(String email) throws RemoteException;
}
