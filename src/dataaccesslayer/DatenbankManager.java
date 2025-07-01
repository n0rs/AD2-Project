/* 
Erstellt bei pgadmin4 eine neue Datenbank mit dem Namen AD2-Projekt
(Rechtsklick auf den Server -> Create -> Database...)
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
*/


package dataaccesslayer;

// Java-Importe
import businesslayer.objekte.Kunde;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import presentationlayer.Presenter;

public class DatenbankManager extends UnicastRemoteObject implements DatenbankManagerInterface {
    public DatenbankManager() throws RemoteException {
        super();
    }

    private static String URL = "jdbc:postgresql://localhost/AD2-Projekt";
    private static String BENUTZERNAME = "postgres";
    private static String PASSWORT = "Davidvilla12*";

    // Verbindung zur Datenbank
    private static Connection connection;

    // Baut die Verbindung zur Datenbank auf
    public void verbindungAufbauen() throws RemoteException {
        try {
            connection = DriverManager.getConnection(URL, BENUTZERNAME, PASSWORT);
            Presenter.printMessage("Verbindung erfolgreich!");
        } catch (SQLException e) {
            Presenter.printError("Fehler bei der Verbindung zur Datenbank: " + e.getMessage());
        }
    }

    // Trennt die Verbindung zur Datenbank
    public void verbindungTrennen() throws RemoteException {
        if (connection != null) {
            try {
                connection.close();
                Presenter.printMessage("Verbindung getrennt!");
            } catch (SQLException e) {
                Presenter.printError("Fehler beim Schließen der Verbindung: " + e.getMessage());
            }
        } else {
            Presenter.printMessage("Keine Verbindung zum Trennen vorhanden.");
        }
    }

    // Legt einen neuen Kunden in der Datenbank an
    @Override
    public void kundeAnlegen(String email, String password) throws RemoteException {
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

    // Löscht einen Kunden anhand der ID
    @Override
    public void kundenLoeschenId(int user_id) throws RemoteException {
        String query = "DELETE FROM nutzer WHERE id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(query)) {
            deleteStmt.setInt(1, user_id);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Löschen der Kunden: " + e.getMessage());
        }
    }

    // Sucht einen Kunden anhand der ID
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

    // Legt einen Eintrag für die E-Mail-Bestätigung an
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

    // Legt einen Eintrag für das Zurücksetzen des Passworts an
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

    // Sucht einen Kunden anhand der E-Mail-Adresse
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

    // Sucht das E-Mail-Token anhand der E-Mail-Adresse
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

    // Sucht das Passwort-Token anhand der E-Mail-Adresse
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

    // Setzt den Status eines Kunden (z.B. aktiviert)
    public void updateStatus(int user_id, boolean isActive) throws RemoteException, NullPointerException {
        String updateQuery ="UPDATE nutzer SET is_active=? WHERE id= ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setBoolean(1, isActive);
            updateStmt.setInt(2, user_id);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            Presenter.printError("Fehler beim Aktivierungslink");
        }
    }

    // Aktualisiert das Passwort eines Kunden
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

    // Löscht das E-Mail-Token eines Kunden
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

    // Löscht das Passwort-Token eines Kunden
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

