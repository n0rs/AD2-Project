package businesslayer.objekte;

import java.io.Serializable;

public class Kunde implements Serializable {
    private int id; // 
    private String email;
    private String password;

    // Konstruktor mit ID (beim Laden aus der DB)
    public Kunde(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Kunde{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    // Getter und Setter 
    public int getId() { 
        return id; 
    }
    public void setId(int id) {
        this.id = id; 
    }

    public String getEmail() {
         return email; 
        }
    public void setEmail(String email) {
         this.email = email; 
        }

    public String getPassword() {
         return password; 
        }
    public void setPassword(String adresse) {
         this.password = adresse; 
        }
}
