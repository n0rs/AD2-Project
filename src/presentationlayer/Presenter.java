package presentationlayer;

public class Presenter {

    public static void printMessage(String message) {
        System.out.println(message);
    }

    public static void printError(String error) {
        System.err.println(error);
    }

    public static String hauptmenuString() {
        return "Willkommen im Hauptmenü!\n" +
               "1. Passwort zurücksetzen\n" +
               "2. Registrierung abschließen\n";
    }

    public static String introString() {
        return "Willkommen bei der Kunderegistrierung!\n" +
               "Bitte geben Sie Ihre E-Mail-Adresse und Ihr Passwort ein, um sich zu registrieren.\n";
    }

    public static void finalizeRegistrierung() {
         System.out.println("1. Token anklicken\n2.60 Minuten warten");
    }

    public static void tokenAbgelaufen() {
        System.out.println("Token nicht innerhalb von einer Stunde angeklickt - Nutzer wird gelöscht");
    }

    
}
