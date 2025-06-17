package businesslayer.service;

import java.util.UUID;

public class TokenErstellung {

    // Methode zur Erstellung eines Tokens
    public static String erstelleToken() {
        // UUID ist eine Klasse, die eine universell eindeutige ID darstellt
        // randomUUID() erstellt eine zufällige UUID die mindestens 128 Bit lang ist
        // toString() wandelt die UUID in einen String um
        return UUID.randomUUID().toString();
    }
}