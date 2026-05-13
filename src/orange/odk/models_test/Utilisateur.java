package orange.odk.models_test;

import orange.odk.models_test.enums.Role;
import java.time.LocalDate;

public class Utilisateur {

    private int    id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    protected Role role;
    protected LocalDate dateInscription;

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String prenom, String email, String motDePasse) {
        this.id         = id;
        this.nom        = nom;
        this.prenom     = prenom;
        this.email      = email;
        this.motDePasse = motDePasse;
        this.role       = Role.UTILISATEUR;
        this.dateInscription = LocalDate.now();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    // Getters / Setters
    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getNom()              { return nom; }
    public void   setNom(String nom)    { this.nom = nom; }

    public String getEmail()                 { return email; }
    public void   setEmail(String email)     { this.email = email; }

    public String getMotDePasse()                    { return motDePasse; }
    public void   setMotDePasse(String motDePasse)   { this.motDePasse = motDePasse; }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", nom='" + nom + "', email='" + email + "'}";
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}