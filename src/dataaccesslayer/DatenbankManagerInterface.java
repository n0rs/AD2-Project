package dataaccesslayer;


import businesslayer.objekte.Kunde;
import java.rmi.RemoteException;

public interface DatenbankManagerInterface extends java.rmi.Remote {
    public void verbindungAufbauen() throws RemoteException;
    public void verbindungTrennen() throws RemoteException;
    public void kundeAnlegen(String email, String passwort) throws RemoteException;
    public void kundenLoeschenId(int user_id) throws RemoteException;
    public void emailVerificationEintragErstellen(int kundenId, String token) throws RemoteException;
    public void passwortResetEintragErstellen(int kundenId, String token) throws RemoteException;
    public Kunde findeKundeNachEmail(String email) throws RemoteException;
    public String findeEmailTokenMitEmail(String email) throws RemoteException;
    public String findePasswortTokenMitEmail(String email) throws RemoteException;
    public void updateStatus(int user_id, boolean isActive) throws RemoteException;
    public void updatePassword(int user_id, String newPassword) throws RemoteException;
    public Kunde findeKundeNachID(int user_id) throws RemoteException;
    public void loescheEmailToken(int kundenId) throws RemoteException;
    public void loeschePasswortToken(int kundenId) throws RemoteException;
}
