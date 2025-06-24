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
import businesslayer.objekte.Kunde;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import presentationlayer.Presenter;


public class DatenbankManager extends UnicastRemoteObject implements DatenbankManagerInterface {
    public DatenbankManager() throws RemoteException {
        super();
    }

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
            Presenter.printError("Fehler beim Laden der Konfigurationsdatei: " + e.getMessage());
            }
        }

    // Verbindung aufbauen
    public void verbindungAufbauen() throws RemoteException {
        try {
            // DriverManager ist eine Klasse, die für die Verwaltung von JDBC-Treibern verantwortlich ist
            // JDBC ist eine API, die es Java-Anwendungen ermöglicht, auf verschiedene Datenbanken zuzugreifen
            // In unserem Fall ist der Treiber in der lib/postgresql-42.7.5.jar Datei enthalten
            connection = DriverManager.getConnection(URL, BENUTZERNAME, PASSWORT);
            Presenter.printMessage("Verbindung erfolgreich!");
        } catch (SQLException e) {
            Presenter.printError("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
        }
    }

    // Verbindung trennen
    public void verbindungTrennen() throws RemoteException {
        if (connection != null) {
            try {
                // close() ist eine Methode der Connection Klasse
                connection.close();
                Presenter.printMessage("Verbindung getrennt!");
            } catch (SQLException e) {
                Presenter.printError("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        } else {
            Presenter.printMessage("Keine Verbindung zum Trennen vorhanden.");
        }
    }

    // Kunde anlegen
    @Override
    public void kundeAnlegen(String email, String password) throws RemoteException {
        // ? ist ein Platzhalter für einen Parameter in der SQL-Abfrage
        // PreparedStatement ist eine Schnittstelle, die SQL-Abfragen mit Platzhaltern unterstützt
        String query = "INSERT INTO nutzer (email, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            Presenter.printMessage("Kunde mit email " + email + " wurde angelegt.");
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Anlegen des Kunden: " + e.getMessage());
        }
    }

    @Override
    public void kundenLoeschenId(int user_id) throws RemoteException {
    String query = "DELETE FROM nutzer WHERE id = ?";
    try (PreparedStatement deleteStmt = connection.prepareStatement(query)) {
        deleteStmt.setInt(1, user_id);
        deleteStmt.executeUpdate();
        Presenter.printMessage("Nutzer mit ID " + user_id + " wurde gelöscht (Verifizierung abgelaufen).");
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Löschen der Kunden: " + e.getMessage());
        }
    }
    
    public Kunde findeKundeNachID(int user_id) throws RemoteException {
        String selectQuery = "SELECT * FROM nutzer WHERE id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, user_id);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String mail = rs.getString("email");
                    String password = rs.getString("password");
                    boolean activated = rs.getBoolean("is_active");
                    return new Kunde(id, mail, password, activated);
                }
            }
        } catch (Exception e) {
            if(e instanceof NullPointerException) {
                return null;
            } else {
            Presenter.printError("Fehler beim Finden des Kunden: " + e.getMessage());
            }
        }
        return null; // Kein Kunde gefunden
    }

    // E-Mail-Verifizierungseintrag erstellen
    public void emailVerificationEintragErstellen(int user_id, String token) throws RemoteException {
        String insertQuery = "INSERT INTO email_verification (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            insertStmt.setInt(1, user_id);
            insertStmt.setString(2, token);
            insertStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis() + 3600000)); // 3600000 1 Stunde gültig
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Erstellen des E-Mail-Verifizierungseintrags: " + e.getMessage());
        }
    }

    // Passwort-Reset-Eintrag erstellen
    public void passwortResetEintragErstellen(int user_id, String token) throws RemoteException {
        String insertQuery = "INSERT INTO password_reset (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, user_id);
            insertStatement.setString(2, token);
            insertStatement.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis() + 3600000)); // 3600000 = 1 Stunde gültig
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Erstellen des Passwort-Reset-Eintrags: " + e.getMessage());
        }
    }

    // Suche nach E-Mail
    public Kunde findeKundeNachEmail(String email) throws RemoteException, NullPointerException {
        String selectQuery = "SELECT * FROM nutzer WHERE email = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String mail = rs.getString("email");
                    String password = rs.getString("password");
                    boolean activated = rs.getBoolean("is_active");
                    return new Kunde(id, mail, password, activated);
                }
            }
        } catch (Exception e) {
            Presenter.printError("Fehler beim Finden des Kunden: " + e.getMessage());
        }
        return null; // Kein Kunde gefunden
    }


    // Finde EmailToken über email
    public String findeEmailTokenMitEmail(String email) throws RemoteException {
        String selectQuery = "SELECT token FROM email_verification WHERE user_id = (SELECT id FROM nutzer WHERE email = ?)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
            }
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Finden des Kunden");
        }
        return null;
    }

    // Finde PasswortToken über email
    public String findePasswortTokenMitEmail(String email) throws RemoteException {
        String selectQuery = "SELECT token FROM password_reset WHERE user_id = (SELECT id FROM nutzer WHERE email = ?)";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, email);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("token");
                }
            }
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Finden des Kunden");
        }
        return null;
    }

    public void updateStatus(int user_id, boolean isActive) throws RemoteException, NullPointerException {
        String updateQuery ="UPDATE nutzer SET is_active=? WHERE id= ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setBoolean(1, isActive);
            updateStmt.setInt(2, user_id);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Aktivierungslink");
        } catch (NullPointerException e) {
           
        }
    }

    @Override
    public void updatePassword(int user_id, String newPassword) throws RemoteException {
         String updateQuery ="UPDATE nutzer SET password = ? WHERE id= ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setString(1, newPassword);
            updateStmt.setInt(2, user_id);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Passwort aktualisieren.");
        }
    }

    @Override
    public void loescheEmailToken(int user_id) throws RemoteException {
        String deleteQuery = "DELETE FROM email_verification WHERE user_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, user_id);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Löschen des E-Mail-Verifizierungseintrags: " + e.getMessage());
        }
    }

    @Override
    public void loeschePasswortToken(int user_id) throws RemoteException {
        String deleteQuery = "DELETE FROM password_reset WHERE user_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, user_id);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Löschen des Passwort-Reset-Eintrags: " + e.getMessage());
        }
    }
}

