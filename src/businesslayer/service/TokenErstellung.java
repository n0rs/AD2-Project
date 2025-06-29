package businesslayer.service;

import java.util.UUID;

public class TokenErstellung {

    // Methode zur Erstellung eines Tokens
    public static String erstelleToken() {
        return UUID.randomUUID().toString();
    }
}