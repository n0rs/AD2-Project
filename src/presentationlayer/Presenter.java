package presentationlayer;

public class Presenter {

    public static void printMessage(String message) {
        System.out.println(message);
    }

    public static void printError(String error) {
        System.err.println(error);
    }

    public static void hauptmenuString() {
        System.out.println("\nHauptmenü:\n" +
               "1. Ausloggen\n" +
               "2. Passwort zurücksetzen\n" +
               "3. Programm beenden\n");
    }

    public static void introString() {
        System.out.println( "Bitte wählen: \n1.Registrieren\n2.Passwort vergessen\n3.Beenden\n"
               );
    }

    public static void signUpString() {
        System.out.println("Willkommen bei der Kunderegistrierung!\n" +
               "Bitte geben Sie Ihre E-Mail-Adresse und Ihr Passwort ein, um sich zu registrieren.\n");
    }

    public static void resetPassword() {
        System.out.println("Neues Passwort: ");
    }

    public static void linkActivation() {
        System.out.println("1. Token anklicken\n2. 60 Minuten warten");
    }

    public static void emailTokenAbgelaufen() {
        System.out.println("Token nicht innerhalb von einer Stunde angeklickt - Nutzer wird gelöscht");
    }

    public static void passwortTokenAbgelaufen() {
        System.out.println("Token nicht innerhalb von einer Stunde angeklickt.");
    }
}