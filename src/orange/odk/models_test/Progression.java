package orange.odk.models_test;

import java.util.Date;

public class Progression {

    private int     id;
    private int     objectifId;
    private Date    dateAction;
    private double  valeur;
    private boolean reussi;

    public Progression() {}

    public Progression(int id, int objectifId, Date dateAction, double valeur, boolean reussi) {
        this.id          = id;
        this.objectifId  = objectifId;
        this.dateAction  = dateAction;
        this.valeur      = valeur;
        this.reussi      = reussi;
    }

    public int     getId()                  { return id; }
    public void    setId(int id)            { this.id = id; }

    public int     getObjectifId()                     { return objectifId; }
    public void    setObjectifId(int objectifId)       { this.objectifId = objectifId; }

    public Date    getDateAction()                     { return dateAction; }
    public void    setDateAction(Date dateAction)      { this.dateAction = dateAction; }

    public double  getValeur()                 { return valeur; }
    public void    setValeur(double valeur)    { this.valeur = valeur; }

    public boolean isReussi()                  { return reussi; }
    public void    setReussi(boolean reussi)   { this.reussi = reussi; }

    @Override
    public String toString() {
        return "Progression{id=" + id + ", objectifId=" + objectifId +
                ", date=" + dateAction + ", valeur=" + valeur + ", reussi=" + reussi + "}";
    }
}