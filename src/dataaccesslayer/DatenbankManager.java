/* 
Erstellt bei pgadmin4 eine neue Datenbank mit dem Namen AD2-Projekt
(Rechtsklick auf den Server -> Create -> Database...)
unter Rechtsklick Properties auf den Server seht sollte wie im Bild die Infos stehen
bzw. der Username und das Passwort legt ihr in der config.properties an
führt die folgenden SQL-Befehle aus, um die Tabellen zu erstellen
CREATE TABLE nutzer (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT FALSE
);
CREATE TABLE email_verification (
    user_id INT REFERENCES nutzer(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id)
);
CREATE TABLE password_reset (
    user_id INT REFERENCES nutzer(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id)
);
config.properties
db.url=jdbc:postgresql://localhost/AD2-Projekt
db.user=hier kommt euer Username rein (z.B. postgres)
db.password=hier kommt euer Passwort rein
(alles ohne "" und leerzeichen)
*/


package dataaccesslayer;

// Java-Importe
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import businesslayer.objekte.Kunde;


public class DatenbankManager {

    private static String URL;
    private static String BENUTZERNAME;
    private static String PASSWORT;

    // Connection ist eine Klasse, die eine Verbindung zu einer Datenbank darstellt
    private static Connection connection;

    // Liest Datenbankkonfiguration aus Datei config.properties
    // Dadurch müssen wir passwort und Benutzername nicht im Code speichern
    // Die Datei config.properties gebe ich euch so, da es unsinnig wäre, sie auf GitHub zu speichern :)
    static {
        try (FileInputStream file = new FileInputStream("config.properties")) {
            Properties properties = new Properties();
            properties.load(file);

            URL = properties.getProperty("db.url");
            BENUTZERNAME = properties.getProperty("db.user");
            PASSWORT = properties.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Konfigurationsdatei: " + e.getMessage());
            }
        }

    // Verbindung aufbauen
    public static void verbindungAufbauen() {
        try {
            // DriverManager ist eine Klasse, die für die Verwaltung von JDBC-Treibern verantwortlich ist
            // JDBC ist eine API, die es Java-Anwendungen ermöglicht, auf verschiedene Datenbanken zuzugreifen
            // In unserem Fall ist der Treiber in der lib/postgresql-42.7.5.jar Datei enthalten
            connection = DriverManager.getConnection(URL, BENUTZERNAME, PASSWORT);
            System.out.println("Verbindung erfolgreich!");
        } catch (SQLException e) {
            System.err.println("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
        }
    }

    // Verbindung trennen
    public static void verbindungTrennen() {
        if (connection != null) {
            try {
                // close() ist eine Methode der Connection Klasse
                connection.close();
                System.out.println("Verbindung getrennt!");
            } catch (SQLException e) {
                System.err.println("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        } else {
            System.out.println("Keine Verbindung zum Trennen vorhanden.");
        }
    }

    // Kunde anlegen
    public static void kundeAnlegen(String email, String password) {
        // ? ist ein Platzhalter für einen Parameter in der SQL-Abfrage
        // PreparedStatement ist eine Schnittstelle, die SQL-Abfragen mit Platzhaltern unterstützt
        String query = "INSERT INTO nutzer (email, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            System.out.println("Kunde mit email " + email + " wurde angelegt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Anlegen des Kunden: " + e.getMessage());
        }
    }

    /// Löscht alle Nutzer mit einer Liste von IDs
    public static void kundenLoeschenIds(List<Integer> userIds) {
    String query = "DELETE FROM nutzer WHERE id = ?";
    try (PreparedStatement deleteStmt = connection.prepareStatement(query)) {
        for (int userId : userIds) {
            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();
            System.out.println("Nutzer mit ID " + userId + " wurde gelöscht (Verifizierung abgelaufen).");
        }
        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen der Kunden: " + e.getMessage());
        }
    }
    
    // Kunden ID anhand der E-Mail finden
    public static int findeKundenId(String email) {
        String selectQuery = "SELECT id FROM nutzer WHERE email = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Finden der Kunden-ID: " + e.getMessage());
        }
        return -1; // -1 bedeutet, dass kein Nutzer gefunden wurde
    }

    // E-Mail-Verifizierungseintrag erstellen
    public static void emailVerificationEintragErstellen(int user_id, String token) {
        String insertQuery = "INSERT INTO email_verification (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, user_id);
            insertStmt.setString(2, token);
            insertStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis() + 60)); // 3600000 1 Stunde gültig
            insertStmt.executeUpdate();
            System.out.println("E-Mail-Verifizierungseintrag für User-ID " + user_id + " wurde erstellt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen des E-Mail-Verifizierungseintrags: " + e.getMessage());
        }
    }

    // Gibt eine Liste aller User-IDs mit abgelaufenem E-Mail-Token zurück
    public static List<Integer> abgelaufeneEmailTokenFinden() {
    List<Integer> userIds = new ArrayList<>();
    String selectQuery = "SELECT user_id FROM email_verification WHERE expires_at < NOW()";
    try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
        ResultSet rs = selectStmt.executeQuery()) {
        while (rs.next()) {
            userIds.add(rs.getInt("user_id"));
        }
    } catch (SQLException e) {
        System.err.println("Fehler beim Finden abgelaufener E-Mail-Token: " + e.getMessage());
    }
    return userIds;
    }

    // Passwort-Reset-Eintrag erstellen
    public static void passwortResetEintragErstellen(int user_id, String token) {
        String insertQuery = "INSERT INTO password_reset (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, user_id);
            insertStatement.setString(2, token);
            insertStatement.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis() + 60)); // 3600000 = 1 Stunde gültig
            insertStatement.executeUpdate();
            System.out.println("Passwort-Reset-Eintrag für User-ID " + user_id + " wurde erstellt.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen des Passwort-Reset-Eintrags: " + e.getMessage());
        }
    }

    // Abgelaufene Passwort-Reset-Token finden
    public static List<Integer> abgelaufenePasswortTokenfinden() {
        List<Integer> userIds = new ArrayList<>();
        String selectQuery = "SELECT user_id FROM password_reset WHERE expires_at < NOW()";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            ResultSet rs = selectStmt.executeQuery()) {
            if (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Finden abgelaufener Passwort-Reset-Token: " + e.getMessage());
        }
        return userIds; 
    }

    // Suche nach E-Mail
    public static Kunde findeKundeNachEmail(String email) {
        String selectQuery = "SELECT id, email, password FROM nutzer WHERE email = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String mail = rs.getString("email");
                    String password = rs.getString("password");
                    return new Kunde(id, mail, password);
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Finden des Kunden: " + e.getMessage());
        }
        return null; // Kein Kunde gefunden
    }

    // Finde EmailToken über email
    public static String findeEmailTokenMitEmail(String email) {
        String selectQuery = "SELECT token FROM email_verification WHERE user_id = (SELECT id FROM nutzer WHERE email = ?)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Finden des Kunden");
        }
        return null;
    }

    // Finde PasswortToken über email
    public static String findePasswortTokenMitEmail(String email) {
        String selectQuery = "SELECT token FROM password_reset WHERE user_id = (SELECT id FROM nutzer WHERE email = ?)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Finden des Kunden");
        }
        return null;
    }
}

