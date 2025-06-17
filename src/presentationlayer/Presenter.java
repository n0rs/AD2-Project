package presentationlayer;

public class Presenter {
    public static String hauptmenuString() {
        return "Willkommen im Hauptmenü!\n" +
               "1. Passwort zurücksetzen\n" +
               "2. Registrierung abschließen\n";
    }

    public static String introString() {
        return "Willkommen bei der Kunderegistrierung!\n" +
               "Bitte geben Sie Ihre E-Mail-Adresse und Ihr Passwort ein, um sich zu registrieren.\n";
    }
}
