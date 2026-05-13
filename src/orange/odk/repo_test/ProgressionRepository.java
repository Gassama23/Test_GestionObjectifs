package orange.odk.repo_test;

import java.sql.SQLException;
import java.util.*;
import orange.odk.dao_test.exceptions.DaoException;
import orange.odk.dao_test.impl.DaoImpl;
import orange.odk.dao_test.interfaces.IDao;
import orange.odk.dao_test.query.QueryBuilder;
import orange.odk.models_test.Progression;

/**
 * ProgressionRepository — accès aux donnees de progression.
 *
 * Depend de l'interface IGenericDao, jamais de l'implementation concrète.
 */
public class ProgressionRepository {

    private static final String TABLE = "progressions";
    private static final String PK    = "id";

    private final IDao dao;

    public ProgressionRepository() throws SQLException {
        this.dao = new DaoImpl();
    }

    /** Constructeur pour injection de dependance (tests, mock…) */
    public ProgressionRepository(IDao dao) {
        this.dao = dao;
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    public Progression create(Progression p) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("objectif_id",  p.getObjectifId());
        donnees.put("date_action",  p.getDateAction());
        donnees.put("valeur",       p.getValeur());
        donnees.put("reussi",       p.isReussi() ? 1 : 0);

        int id = dao.insert(TABLE, donnees);
        p.setId(id);
        return p;
    }

    public Progression findById(int id) throws DaoException {
        Map<String, Object> row = dao.findById(TABLE, PK, id);
        return row == null ? null : mapper(row);
    }

    public List<Progression> findAll() throws DaoException {
        List<Progression> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findAll(TABLE)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    public Progression update(Progression p) throws DaoException {
        Map<String, Object> donnees = new LinkedHashMap<>();
        donnees.put("date_action", p.getDateAction());
        donnees.put("valeur",      p.getValeur());
        donnees.put("reussi",      p.isReussi() ? 1 : 0);

        dao.update(TABLE, PK, p.getId(), donnees);
        return p;
    }

    public boolean delete(int id) throws DaoException {
        return dao.delete(TABLE, PK, id);
    }

    public boolean exists(int id) throws DaoException {
        return dao.exists(TABLE, PK, id);
    }

    // -------------------------------------------------------------------------
    // REQUÊTES MeTIER SIMPLES
    // -------------------------------------------------------------------------

    public List<Progression> findByObjectif(int objectifId) throws DaoException {
        List<Progression> liste = new ArrayList<>();
        for (Map<String, Object> row : dao.findBy(TABLE, "objectif_id", objectifId)) {
            liste.add(mapper(row));
        }
        return liste;
    }

    public int countJoursReussis(int objectifId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("COUNT(*) AS total")
                .from(TABLE)
                .where("objectif_id = ? AND reussi = 1", objectifId);

        return dao.executeCount(query);
    }

    // -------------------------------------------------------------------------
    // REQUÊTES AVEC JOINTURES (QueryBuilder)
    // -------------------------------------------------------------------------

    /**
     * Historique complet d'un objectif avec le titre de l'objectif.
     * JOIN objectifs
     */
    public List<Map<String, Object>> findHistoriqueAvecObjectif(int objectifId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("p.*", "o.titre AS objectif_titre", "o.domaine")
                .from(TABLE, "p")
                .join("objectifs", "o", "p.objectif_id = o.id")
                .where("p.objectif_id = ?", objectifId)
                .orderBy("p.date_action DESC");

        return dao.executeQuery(query);
    }

    /**
     * Toutes les progressions d'un utilisateur (via ses objectifs).
     * JOIN objectifs → utilisateurs
     */
    public List<Map<String, Object>> findByUtilisateur(int utilisateurId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("p.*", "o.titre AS objectif_titre")
                .from(TABLE, "p")
                .join("objectifs", "o", "p.objectif_id = o.id")
                .where("o.utilisateur_id = ?", utilisateurId)
                .orderBy("p.date_action DESC");

        return dao.executeQuery(query);
    }

    /**
     * Calcule le streak actuel (jours consecutifs reussis) pour un objectif.
     * Le streak s'arrête au premier jour non reussi en remontant depuis aujourd'hui.
     */
    public int getCurrentStreak(int objectifId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("date_action", "reussi")
                .from(TABLE)
                .where("objectif_id = ?", objectifId)
                .orderBy("date_action DESC");

        List<Map<String, Object>> progressions = dao.executeQuery(query);

        int streak = 0;
        for (Map<String, Object> row : progressions) {
            Object reussi = row.get("reussi");
            boolean estReussi = (reussi instanceof Number && ((Number) reussi).intValue() == 1)
                    || Boolean.TRUE.equals(reussi);
            if (estReussi) {
                streak++;
            } else {
                break; // serie interrompue
            }
        }
        return streak;
    }

    /**
     * Resume statistique d'un objectif :
     * total de jours, jours reussis, valeur cumulee, valeur moyenne.
     */
    public Map<String, Object> getResume(int objectifId) throws DaoException {
        QueryBuilder query = new QueryBuilder()
                .select("COUNT(*)           AS total_jours",
                        "SUM(reussi)        AS jours_reussis",
                        "SUM(valeur)        AS valeur_cumulee",
                        "AVG(valeur)        AS valeur_moyenne")
                .from(TABLE)
                .where("objectif_id = ?", objectifId);

        return dao.executeQueryOne(query);
    }

    // -------------------------------------------------------------------------
    // MAPPING
    // -------------------------------------------------------------------------

    private Progression mapper(Map<String, Object> row) {
        Progression p = new Progression();
        p.setId(         toInt(row.get("id")));
        p.setObjectifId( toInt(row.get("objectif_id")));
        p.setDateAction( toDate(row.get("date_action")));
        p.setValeur(     toDouble(row.get("valeur")));

        Object reussi = row.get("reussi");
        p.setReussi((reussi instanceof Number && ((Number) reussi).intValue() == 1)
                || Boolean.TRUE.equals(reussi));
        return p;
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