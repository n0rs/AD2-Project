package businesslayer.service;

public class EmailVersand {
    public static void sendeRegistrierungsEmail(String token) {
        String email = "Vielen Dank für Ihre Registrierung. Wir freuen uns, Sie als neuen Kunden begrüßen zu dürfen.\n" +
                       "Bitte schließen Sie Ihre Registrierung ab, indem Sie auf den folgenden token klicken:\n" + token + "\n";
        System.out.println(email);
    }
}
