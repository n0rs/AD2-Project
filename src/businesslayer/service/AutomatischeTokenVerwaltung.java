package businesslayer.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import dataaccesslayer.DatenbankManager;

// Diese Klasse läuft auf einem separaten Thread und führt alle 10 Minuten die Methode aus
public class AutomatischeTokenVerwaltung {
    public static void automatischerExecutorEmail() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if(!DatenbankManager.abgelaufeneEmailTokenFinden().isEmpty()) {
                DatenbankManager.kundenLoeschenIds(DatenbankManager.abgelaufeneEmailTokenFinden());
            }
        }, 0, 2, TimeUnit.MINUTES);
    }

    public static void automatischerExecutorPasswort() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if(!DatenbankManager.abgelaufenePasswortTokenfinden().isEmpty()) {
                DatenbankManager.kundenLoeschenIds(DatenbankManager.abgelaufenePasswortTokenfinden());
            }
        }, 0, 2, TimeUnit.MINUTES);
    }
}
