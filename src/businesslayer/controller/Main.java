// javac -cp "lib/*" -d ./bin ./webshop/Main.java ./webshop/businessLayer/Objekte/*.java ./webshop/businessLayer/service/*.java ./webshop/businessLayer/validation/*.java ./webshop/dataAccessLayer/*.java ./webshop/presentationLayer/*.java kompiliert Programm
// java -cp ".\bin;lib\*" webshop.Main füht Main.java aus

package businesslayer.controller;



import businesslayer.objekte.Kunde;
import businesslayer.service.AutomatischeTokenVerwaltung;
import businesslayer.service.EmailPruefer;
import businesslayer.service.PasswortPruefer;
import businesslayer.service.TokenErstellung;
import dataaccesslayer.DatenbankManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
        String email;
        String passwort;
        // Verbindung zur Datenbank herstellen und eine Abfrage durchführen
        DatenbankManager.verbindungAufbauen();
        AutomatischeTokenVerwaltung.automatischerExecutorEmail();
        AutomatischeTokenVerwaltung.automatischerExecutorPasswort();

        email = EmailPruefer.starteEmailPruefung(scanner);
        passwort = PasswortPruefer.startePasswortPruefung(scanner);

        Kunde kunde = new Kunde(email, passwort);
        Kunde kunde2 = new Kunde("max@beispiel.de", "Passwort123!");
        DatenbankManager.kundeAnlegen(kunde.getEmail(), kunde.getPassword());
        DatenbankManager.kundeAnlegen(kunde2.getEmail(), kunde2.getPassword());
        kunde = DatenbankManager.findeKundeNachEmail(kunde.getEmail());
        kunde2 = DatenbankManager.findeKundeNachEmail(kunde2.getEmail());
        System.out.println(kunde.getId());
        System.out.println(kunde2.getId());
        DatenbankManager.emailVerificationEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
        DatenbankManager.passwortResetEintragErstellen(kunde.getId(), TokenErstellung.erstelleToken());
        DatenbankManager.emailVerificationEintragErstellen(kunde2.getId(), TokenErstellung.erstelleToken());
        DatenbankManager.passwortResetEintragErstellen(kunde2.getId(), TokenErstellung.erstelleToken());

        System.out.println(DatenbankManager.findeEmailTokenMitEmail(email)); 
        System.out.println(DatenbankManager.findePasswortTokenMitEmail(email));


        DatenbankManager.verbindungTrennen();
        System.exit(0);
    }
    }
}