package orange.odk.models_test;

import java.util.Date;

public class Objectif {

    public enum Domaine { APPRENTISSAGE, ECONOMIE, SPORT, DEVELOPPEMENT }
    public enum Statut  { EN_COURS, TERMINE, ABANDONNE }

    private int     id;
    private int     utilisateurId;
    private String  titre;
    private Domaine domaine;
    private double  cible;
    private Date    dateDebut;
    private Date    dateFin;
    private Statut  statut;

    public Objectif() {}

    public Objectif(int id, int utilisateurId, String titre, Domaine domaine,
                    double cible, Date dateDebut, Date dateFin, Statut statut) {
        this.id             = id;
        this.utilisateurId  = utilisateurId;
        this.titre          = titre;
        this.domaine        = domaine;
        this.cible          = cible;
        this.dateDebut      = dateDebut;
        this.dateFin        = dateFin;
        this.statut         = statut;
    }

    // Getters / Setters
    public int     getId()                  { return id; }
    public void    setId(int id)            { this.id = id; }

    public int     getUtilisateurId()                      { return utilisateurId; }
    public void    setUtilisateurId(int utilisateurId)     { this.utilisateurId = utilisateurId; }

    public String  getTitre()                  { return titre; }
    public void    setTitre(String titre)      { this.titre = titre; }

    public Domaine getDomaine()                    { return domaine; }
    public void    setDomaine(Domaine domaine)     { this.domaine = domaine; }

    public double  getCible()                  { return cible; }
    public void    setCible(double cible)      { this.cible = cible; }

    public Date    getDateDebut()                    { return dateDebut; }
    public void    setDateDebut(Date dateDebut)      { this.dateDebut = dateDebut; }

    public Date    getDateFin()                { return dateFin; }
    public void    setDateFin(Date dateFin)    { this.dateFin = dateFin; }

    public Statut  getStatut()                  { return statut; }
    public void    setStatut(Statut statut)     { this.statut = statut; }

    @Override
    public String toString() {
        return "Objectif{id=" + id + ", titre='" + titre + "', domaine=" + domaine +
                ", statut=" + statut + ", cible=" + cible + "}";
    }
}