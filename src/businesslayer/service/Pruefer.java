// Interface
package businesslayer.service;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface Pruefer {
    static boolean pruefe(String wert) throws RemoteException, MalformedURLException, NotBoundException {
        return false;
    }
}