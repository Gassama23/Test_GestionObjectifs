package orange.odk.models_test;

import java.util.Date;

public class Badge {

    private int    id;
    private String nom;
    private String description;
    private Date   dateObtention; // renseigne quand recupere via utilisateur_badges

    public Badge() {}

    public Badge(int id, String nom, String description) {
        this.id          = id;
        this.nom         = nom;
        this.description = description;
    }

    public int    getId()                { return id; }
    public void   setId(int id)          { this.id = id; }

    public String getNom()               { return nom; }
    public void   setNom(String nom)     { this.nom = nom; }

    public String getDescription()                     { return description; }
    public void   setDescription(String description)   { this.description = description; }

    public Date   getDateObtention()                       { return dateObtention; }
    public void   setDateObtention(Date dateObtention)     { this.dateObtention = dateObtention; }

    @Override
    public String toString() {
        return "Badge{id=" + id + ", nom='" + nom + "', description='" + description + "'}";
    }
}