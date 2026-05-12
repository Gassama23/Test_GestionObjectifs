package orange.odk.repo_test;

import java.sql.SQLException;
import java.util.*;
import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.dao_test.impl.DaoImpl;
import orange.odk.dao_test.interfaces.IDao;
import orange.odk.dao_test.query.QueryBuilder;
import orange.odk.models_test.Objectif;

/**
 * ObjectifRepository — accès aux donnees des objectifs.
 *
 * Depend de l'interface IGenericDao, jamais de l'implementation concrète.
 */
public class ObjectifRepository {

    private static final String TABLE = "objectifs";
    private static final String PK    = "id";

    private final IDao dao;

    public ObjectifRepository() throws SQLException {
        this.dao = new DaoImpl();
    }

    /** Constructeur pour injection de dependance (tests, mock…) */
    public ObjectifRepository(IDao dao) {
        this.dao = dao;
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    public Objectif create(Objectif o) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("utilisateur_id", o.getUtilisateurId());
        ajtDonnee(o, donnees);

        int id = dao.insert(TABLE, donnees);
        o.setId(id);
        return o;
    }

    private void ajtDonnee(Objectif o, Map<String, Object> donnees) {
        donnees.put("titre",          o.getTitre());
        donnees.put("domaine",        o.getDomaine().name());
        donnees.put("cible",          o.getCible());
        donnees.put("date_debut",     o.getDateDebut());
        donnees.put("date_fin",       o.getDateFin());
        donnees.put("statut",         o.getStatut().name());
    }

    public Objectif findById(int id) throws DaoException {
        Map<String, Object> row = dao.findById(TABLE, PK, id);
        return row == null ? null : mapper(row);
    }

    public List<Objectif> findAll() throws DaoException {
        List<Objectif> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findAll(TABLE)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    public Objectif update(Objectif o) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        ajtDonnee(o, donnees);

        dao.update(TABLE, PK, o.getId(), donnees);
        return o;
    }

    public boolean delete(int id) throws DaoException {
        return dao.delete(TABLE, PK, id);
    }

    public boolean exists(int id) throws DaoException {
        return dao.exists(TABLE, PK, id);
    }

    public int count() throws DaoException {
        return dao.count(TABLE);
    }

    // -------------------------------------------------------------------------
    // REQUÊTES MeTIER SIMPLES
    // -------------------------------------------------------------------------

    public List<Objectif> findByUtilisateur(int utilisateurId) throws DaoException {
        List<Objectif> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findBy(TABLE, "utilisateur_id", utilisateurId)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    public List<Objectif> findByStatut(Objectif.Statut statut) throws DaoException {
        List<Objectif> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findBy(TABLE, "statut", statut.name())) {
            liste.add(mapper(row));
        }
        return liste;
    }

    public List<Objectif> findByDomaine(Objectif.Domaine domaine) throws DaoException {
        List<Objectif> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findBy(TABLE, "domaine", domaine.name())) {
            liste.add(mapper(row));
        }
        return liste;
    }

    public List<Objectif> findByUtilisateurEtStatut(int utilisateurId, Objectif.Statut statut) throws DaoException {
        Map<String, Object> filtres = new LinkedHashMap<>();
        filtres.put("utilisateur_id", utilisateurId);
        filtres.put("statut",         statut.name());

        List<Objectif> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findByMultiple(TABLE, filtres)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    // -------------------------------------------------------------------------
    // REQUÊTES AVEC JOINTURES (QueryBuilder)
    // -------------------------------------------------------------------------

    /**
     * Recupère tous les objectifs d'un utilisateur avec le nombre de jours reussis.
     * JOIN progressions + COUNT
     */
    public List<Map<String, Object>> findAvecProgression(int utilisateurId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("o.*",
                        "COUNT(p.id)                                    AS total_jours",
                        "SUM(CASE WHEN p.reussi = 1 THEN 1 ELSE 0 END) AS jours_reussis")
                .from(TABLE, "o")
                .leftJoin("progressions", "p", "p.objectif_id = o.id")
                .where("o.utilisateur_id = ?", utilisateurId)
                .groupBy("o.id")
                .orderBy("o.date_debut DESC");

        return dao.executeQuery(query);
    }

    /**
     * Recupère un objectif complet avec les infos de son utilisateur.
     * JOIN utilisateurs
     */
    public Map<String, Object> findAvecUtilisateur(int objectifId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("o.*", "u.nom AS utilisateur_nom", "u.email AS utilisateur_email")
                .from(TABLE, "o")
                .join("utilisateurs", "u", "o.utilisateur_id = u.id")
                .where("o.id = ?", objectifId);

        return dao.executeQueryOne(query);
    }

    /**
     * Recupère les objectifs en retard :
     * statut EN_COURS et date_fin depassee.
     */
    public List<Map<String, Object>> findEnRetard(int utilisateurId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("o.*", "u.nom AS utilisateur_nom")
                .from(TABLE, "o")
                .join("utilisateurs", "u", "o.utilisateur_id = u.id")
                .where("o.utilisateur_id = ?", utilisateurId)
                .where("o.statut = ?", Objectif.Statut.EN_COURS.name())
                .where("o.date_fin < CURDATE()");

        return dao.executeQuery(query);
    }

    /**
     * Statistiques globales de tous les objectifs d'un utilisateur.
     * Retourne : total, en_cours, termines, abandonnes
     */
    public Map<String, Object> getStatistiques(int utilisateurId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("COUNT(*) AS total",
                        "SUM(CASE WHEN statut = 'EN_COURS'   THEN 1 ELSE 0 END) AS en_cours",
                        "SUM(CASE WHEN statut = 'TERMINE'    THEN 1 ELSE 0 END) AS termines",
                        "SUM(CASE WHEN statut = 'ABANDONNE'  THEN 1 ELSE 0 END) AS abandonnes")
                .from(TABLE)
                .where("utilisateur_id = ?", utilisateurId);

        return dao.executeQueryOne(query);
    }

    // -------------------------------------------------------------------------
    // MAPPING
    // -------------------------------------------------------------------------

    private Objectif mapper(Map<String, Object> row) {
        Objectif o = new Objectif();
        o.setId(            toInt(row.get("id")));
        o.setUtilisateurId( toInt(row.get("utilisateur_id")));
        o.setTitre(         (String) row.get("titre"));
        o.setDomaine(       Objectif.Domaine.valueOf((String) row.get("domaine")));
        o.setCible(         toDouble(row.get("cible")));
        o.setDateDebut(     toDate(row.get("date_debut")));
        o.setDateFin(       toDate(row.get("date_fin")));
        o.setStatut(        Objectif.Statut.valueOf((String) row.get("statut")));
        return o;
    }

    private int toInt(Object val) {
        return val instanceof Number ? ((Number) val).intValue() : 0;
    }

    private double toDouble(Object val) {
        return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
    }

    private java.util.Date toDate(Object val) {
        if (val instanceof java.sql.Date) return new java.util.Date(((java.sql.Date) val).getTime());
        if (val instanceof java.util.Date) return (java.util.Date) val;
        return null;
    }
}