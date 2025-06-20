// Interface
package businesslayer.service;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface Pruefer {
    boolean pruefe(String wert) throws RemoteException, MalformedURLException, NotBoundException;
    boolean checkUniqueness(String wert1, String wert2) throws RemoteException, MalformedURLException, NotBoundException;
}