package dataaccesslayer;


import businesslayer.objekte.Kunde;
import java.rmi.RemoteException;

public interface DatenbankManagerInterface extends java.rmi.Remote {
    public abstract void verbindungAufbauen() throws RemoteException;
    public abstract void verbindungTrennen() throws RemoteException;
    public abstract void kundeAnlegen(String email, String passwort) throws RemoteException;
    public abstract void kundenLoeschenId(int user_id) throws RemoteException;
    public abstract void emailVerificationEintragErstellen(int kundenId, String token) throws RemoteException;
    public abstract void passwortResetEintragErstellen(int kundenId, String token) throws RemoteException;
    public abstract Kunde findeKundeNachEmail(String email) throws RemoteException;
    public abstract String findeEmailTokenMitEmail(String email) throws RemoteException;
    public abstract String findePasswortTokenMitEmail(String email) throws RemoteException;
    public abstract void updateStatus(int user_id, boolean isActive) throws RemoteException;
    public abstract void updatePassword(int user_id, String newPassword) throws RemoteException;
    public abstract Kunde findeKundeNachID(int user_id) throws RemoteException;
    public abstract void loescheEmailToken(int kundenId) throws RemoteException;
    public abstract void loeschePasswortToken(int kundenId) throws RemoteException;
}
